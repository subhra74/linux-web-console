/**
 * 
 */
package web.console.app.files;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.console.app.files.search.SearchResult;

/**
 * @author subhro
 *
 */
@Service
public class FileService {

	private boolean posix;

	/**
	 * 
	 */
	public FileService() {
		posix = !System.getProperty("os.name").toLowerCase()
				.contains("windows");
	}

	@Autowired
	private SearchOperations searchOp;

	@Autowired
	private FileTypeDetector typeDetector;

	public String createSearch(String folder, String searchText) {
		return searchOp.createSearchTask(folder, searchText);
	}

	public void cancelSearch(String id) {
		searchOp.cancelSearch(id);
	}

	public SearchResult getSearchResult(String id, int fileIndex,
			int folderIndex) {
		return searchOp.getSearchResult(id, fileIndex, folderIndex);
	}

	private String getFileType(File f) {
		return typeDetector.getType(f);
	}

	public List<FileInfo> list(String path) {
		System.out.println("Listing file: " + path);
		File[] files = new File(path).listFiles();
		List<FileInfo> list = new ArrayList<>();
		if (files == null) {
			return list;
		}
		for (File f : files) {
			FileInfo info = new FileInfo(f.getName(), f.getAbsolutePath(), null,
					null, f.isDirectory() ? "Directory" : getFileType(f),
					f.length(), -1, new Date(f.lastModified()), posix);
			Set<PosixFilePermission> filePerm = null;
			try {
				filePerm = Files.getPosixFilePermissions(f.toPath());
				String permission = PosixFilePermissions.toString(filePerm);
				info.setPermissionString(permission);

				FileOwnerAttributeView fv = Files.getFileAttributeView(
						f.toPath(), FileOwnerAttributeView.class);
				info.setUser(fv.getOwner().getName());
			} catch (Exception e) {
				// e.printStackTrace();
				info.setPermissionString("---");
			}

			list.add(info);
		}
		list.sort((FileInfo a, FileInfo b) -> {
			String type1 = a.getType();
			String type2 = b.getType();
			if (type1.equals("Directory") && type2.equals("Directory")) {
				return 0;
			} else if (type1.equals("Directory")) {
				return -1;
			} else if (type2.equals("Directory")) {
				return 1;
			} else {
				return 0;
			}
		});
		return list;
	}

	public void setText(String path, String text) throws Exception {
		Files.writeString(Paths.get(path), text);
	}

	public String getText(String path) throws Exception {
		return new String(Files.readAllBytes(Paths.get(path)), "utf-8");
	}
}
