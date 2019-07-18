/**
 * 
 */
package cloudshell.app.health;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * @author subhro
 *
 */
@Component
public class SystemHealthMonitor {
	/**
	 * 
	 */
	private CentralProcessor processor;
	private GlobalMemory memory;
	private OperatingSystem os;
	private boolean isWindows;

	private SystemInfo si;

	public SystemHealthMonitor() {
		si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		os = si.getOperatingSystem();
		processor = hal.getProcessor();
		memory = hal.getMemory();
		processor.getSystemCpuLoadBetweenTicks();
		isWindows = System.getProperty("os.name").toLowerCase()
				.contains("windows");
	}

	public synchronized SystemStats getStats() {
		SystemStats stats = new SystemStats();
		double cpuUsed = processor.getSystemCpuLoadBetweenTicks() * 100;

		stats.setCpuUsed(cpuUsed);
		stats.setCpuFree(100 - cpuUsed);

		long avail = memory.getAvailable();
		long total = memory.getTotal();
		if (total > 0) {
			double memoryUsed = ((total - avail) * 100) / total;
			stats.setMemoryUsed(memoryUsed);
			stats.setMemoryFree(100 - memoryUsed);
		}

		File f = new File("/");
		long totalDiskSpace = f.getTotalSpace();
		long freeDiskSpace = f.getFreeSpace();
		if (totalDiskSpace > 0) {
			double diskUsed = ((totalDiskSpace - freeDiskSpace) * 100)
					/ totalDiskSpace;
			stats.setDiskUsed(diskUsed);
			stats.setDiskFree(100 - diskUsed);
		}

		long totalSwap = memory.getSwapTotal();
		long usedSwap = memory.getSwapUsed();
		if (totalSwap > 0) {
			double swapUsed = usedSwap * 100 / totalSwap;
			stats.setSwapUsed(swapUsed);
			stats.setSwapFree(100 - swapUsed);
		}

		return stats;
	}

	private String formatCmd(String args) {
		StringBuilder sb = new StringBuilder();
		for (char ch : args.toCharArray()) {
			if (ch == 0) {
				sb.append(" ");
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	public synchronized List<ProcessInfo> getProcessList() {
		OSProcess[] procs = os.getProcesses(0, null, false);
		List<ProcessInfo> list = new ArrayList<>();
		if (procs != null && procs.length > 0) {
			for (OSProcess proc : procs) {
				ProcessInfo info = new ProcessInfo();
				info.setPid(proc.getProcessID());
				info.setName(proc.getName());
				info.setCommand(formatCmd(proc.getCommandLine()));
				info.setCpuUsage(proc.calculateCpuPercent());
				info.setMemoryUsage(proc.getResidentSetSize());
				info.setVmUsage(proc.getVirtualSize());
				info.setState(proc.getState().toString());
				info.setStartTime(proc.getStartTime());
				info.setUser(proc.getUser());
				info.setPriority(proc.getPriority());
				list.add(info);
			}
		}
		return list;
	}

	private boolean killProcess(int pid) {
		ProcessBuilder pb = new ProcessBuilder(
				isWindows ? Arrays.asList("taskkill", "/pid", pid + "", "/f")
						: Arrays.asList("kill", "-9", pid + ""));
		try {
			Process proc = pb.start();
			int ret = proc.waitFor();
			return ret == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean killProcess(List<Integer> pidList) {
		boolean success = true;
		for (Integer pid : pidList) {
			if (success) {
				success = killProcess(pid);
			}
		}
		return success;
	}
}
