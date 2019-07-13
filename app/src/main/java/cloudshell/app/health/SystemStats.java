/**
 * 
 */
package cloudshell.app.health;

/**
 * @author subhro
 *
 */
public class SystemStats {
	private double cpuUsed = -1, cpuFree = -1, memoryUsed = -1, memoryFree = -1,
			swapUsed = -1, swapFree = -1, diskUsed = -1, diskFree = -1;

	/**
	 * @return the cpuUsed
	 */
	public double getCpuUsed() {
		return cpuUsed;
	}

	/**
	 * @param cpuUsed the cpuUsed to set
	 */
	public void setCpuUsed(double cpuUsed) {
		this.cpuUsed = cpuUsed;
	}

	/**
	 * @return the cpuFree
	 */
	public double getCpuFree() {
		return cpuFree;
	}

	/**
	 * @param cpuFree the cpuFree to set
	 */
	public void setCpuFree(double cpuFree) {
		this.cpuFree = cpuFree;
	}

	/**
	 * @return the memoryUsed
	 */
	public double getMemoryUsed() {
		return memoryUsed;
	}

	/**
	 * @param memoryUsed the memoryUsed to set
	 */
	public void setMemoryUsed(double memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	/**
	 * @return the memoryFree
	 */
	public double getMemoryFree() {
		return memoryFree;
	}

	/**
	 * @param memoryFree the memoryFree to set
	 */
	public void setMemoryFree(double memoryFree) {
		this.memoryFree = memoryFree;
	}

	/**
	 * @return the swapUsed
	 */
	public double getSwapUsed() {
		return swapUsed;
	}

	/**
	 * @param swapUsed the swapUsed to set
	 */
	public void setSwapUsed(double swapUsed) {
		this.swapUsed = swapUsed;
	}

	/**
	 * @return the swapFree
	 */
	public double getSwapFree() {
		return swapFree;
	}

	/**
	 * @param swapFree the swapFree to set
	 */
	public void setSwapFree(double swapFree) {
		this.swapFree = swapFree;
	}

	/**
	 * @return the diskUsed
	 */
	public double getDiskUsed() {
		return diskUsed;
	}

	/**
	 * @param diskUsed the diskUsed to set
	 */
	public void setDiskUsed(double diskUsed) {
		this.diskUsed = diskUsed;
	}

	/**
	 * @return the diskFree
	 */
	public double getDiskFree() {
		return diskFree;
	}

	/**
	 * @param diskFree the diskFree to set
	 */
	public void setDiskFree(double diskFree) {
		this.diskFree = diskFree;
	}
}
