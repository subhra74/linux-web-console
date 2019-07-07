/**
 * 
 */
package cloudshell.app.files.copy;

import java.util.List;

/**
 * @author subhro
 *
 */
public class FileCopyRequest {
	private List<String> sourceFile;
	private String targetFolder;

	/**
	 * @return the sourceFile
	 */
	public List<String> getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile the sourceFile to set
	 */
	public void setSourceFile(List<String> sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the targetFolder
	 */
	public String getTargetFolder() {
		return targetFolder;
	}

	/**
	 * @param targetFolder the targetFolder to set
	 */
	public void setTargetFolder(String targetFolder) {
		this.targetFolder = targetFolder;
	}
}
