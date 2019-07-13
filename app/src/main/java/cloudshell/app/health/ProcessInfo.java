/**
 * 
 */
package cloudshell.app.health;

/**
 * @author subhro
 *
 */
public class ProcessInfo {
	private String name, command, user, state;
	private int pid, priority;
	private double cpuUsage, memoryUsage, vmUsage;
	private long startTime;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @return the cpuUsage
	 */
	public double getCpuUsage() {
		return cpuUsage;
	}

	/**
	 * @param cpuUsage the cpuUsage to set
	 */
	public void setCpuUsage(double cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	/**
	 * @return the memoryUsage
	 */
	public double getMemoryUsage() {
		return memoryUsage;
	}

	/**
	 * @param memoryUsage the memoryUsage to set
	 */
	public void setMemoryUsage(double memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	/**
	 * @return the vmUsage
	 */
	public double getVmUsage() {
		return vmUsage;
	}

	/**
	 * @param vmUsage the vmUsage to set
	 */
	public void setVmUsage(double vmUsage) {
		this.vmUsage = vmUsage;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
