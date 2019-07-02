/**
 * 
 */
package webshell.app.files.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author subhro
 *
 */
public class SearchResult {
	/**
	 * @param isDone
	 * @param files
	 * @param folders
	 */
	public SearchResult(boolean isDone, List<String> files,
			List<String> folders) {
		super();
		this.isDone = isDone;
		this.files = files;
		this.folders = folders;
	}

	private boolean isDone;
	private List<String> files = new ArrayList<>(), folders = new ArrayList<>();

	/**
	 * @return the isDone
	 */
	public boolean isDone() {
		return isDone;
	}

	/**
	 * @param isDone the isDone to set
	 */
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	/**
	 * @return the files
	 */
	public List<String> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<String> files) {
		this.files = files;
	}

	/**
	 * @return the folders
	 */
	public List<String> getFolders() {
		return folders;
	}

	/**
	 * @param folders the folders to set
	 */
	public void setFolders(List<String> folders) {
		this.folders = folders;
	}
}
