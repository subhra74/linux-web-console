/**
 * 
 */
package web.console.app.files.copy;

/**
 * @author subhro
 *
 */
public class FileCopyProgressResponse {
	private int progress;
	private String status;
	private String name;
	private String errors;
	private boolean hasErrors;
	private String id;

	/**
	 * @param progress
	 * @param status
	 * @param errors
	 * @param hasErrors
	 */
	public FileCopyProgressResponse(String id, String name, int progress,
			String status, String errors, boolean hasErrors) {
		super();
		this.id = id;
		this.name = name;
		this.progress = progress;
		this.status = status;
		this.errors = errors;
		this.hasErrors = hasErrors;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the errors
	 */
	public String getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(String errors) {
		this.errors = errors;
	}

	/**
	 * @return the hasErrors
	 */
	public boolean isHasErrors() {
		return hasErrors;
	}

	/**
	 * @param hasErrors the hasErrors to set
	 */
	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
}
