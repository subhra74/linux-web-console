/**
 * 
 */
package web.console.app.files.search;

import java.util.ArrayList;
import java.util.List;

import web.console.app.files.FileInfo;

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
	public SearchResult(boolean isDone, List<FileInfo> files) {
		super();
		this.isDone = isDone;
		this.files = files;
	}

	private boolean isDone;
	private List<FileInfo> files = new ArrayList<>();

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
	public List<FileInfo> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}
}
