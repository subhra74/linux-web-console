/**
 * 
 */
package cloudshell.app.files;

import java.util.Date;

/**
 * @author subhro
 *
 */
public class FileInfo {

	private String name, path, permissionString, user, type;
	private long size;
	private Date lastModified;
	private boolean posix;

	/**
	 * @param name
	 * @param path
	 * @param permissionString
	 * @param user
	 * @param type
	 * @param size
	 * @param permission
	 * @param lastModified
	 */
	public FileInfo(String name, String path, String permissionString,
			String user, String type, long size, long permission,
			Date lastModified, boolean posix) {
		super();
		this.name = name;
		this.path = path;
		this.permissionString = permissionString;
		this.user = user;
		this.type = type;
		this.size = size;
		this.lastModified = lastModified;
		this.posix = posix;
	}

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
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the permissionString
	 */
	public String getPermissionString() {
		return permissionString;
	}

	/**
	 * @param permissionString the permissionString to set
	 */
	public void setPermissionString(String permissionString) {
		this.permissionString = permissionString;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the posix
	 */
	public boolean isPosix() {
		return posix;
	}

	/**
	 * @param posix the posix to set
	 */
	public void setPosix(boolean posix) {
		this.posix = posix;
	}
}
