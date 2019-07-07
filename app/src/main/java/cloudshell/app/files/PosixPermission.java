/**
 * 
 */
package cloudshell.app.files;

/**
 * @author subhro
 *
 */
public class PosixPermission {
	private String owner, group;
	private String ownerAccess, groupAccess, otherAccess;
	private boolean executable;

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the ownerAccess
	 */
	public String getOwnerAccess() {
		return ownerAccess;
	}

	/**
	 * @param ownerAccess the ownerAccess to set
	 */
	public void setOwnerAccess(String ownerAccess) {
		this.ownerAccess = ownerAccess;
	}

	/**
	 * @return the groupAccess
	 */
	public String getGroupAccess() {
		return groupAccess;
	}

	/**
	 * @param groupAccess the groupAccess to set
	 */
	public void setGroupAccess(String groupAccess) {
		this.groupAccess = groupAccess;
	}

	/**
	 * @return the otherAccess
	 */
	public String getOtherAccess() {
		return otherAccess;
	}

	/**
	 * @param otherAccess the otherAccess to set
	 */
	public void setOtherAccess(String otherAccess) {
		this.otherAccess = otherAccess;
	}

	/**
	 * @return the executable
	 */
	public boolean isExecutable() {
		return executable;
	}

	/**
	 * @param executable the executable to set
	 */
	public void setExecutable(boolean executable) {
		this.executable = executable;
	}
}
