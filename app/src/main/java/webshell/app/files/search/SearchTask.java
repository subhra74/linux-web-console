/**
 * 
 */
package webshell.app.files.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import webshell.app.files.FileInfo;

/**
 * @author subhro
 *
 */
public class SearchTask implements Runnable {

	private boolean posix;

	/**
	 * @param folder
	 * @param searchText
	 */
	public SearchTask(String folder, String searchText) {
		super();
		this.id = UUID.randomUUID().toString();
		if (this.folder == null || this.folder.length() < 1) {
			this.folder = System.getProperty("user.home");
		} else {
			this.folder = folder;
		}

		this.searchText = searchText.toLowerCase(Locale.ENGLISH);
		posix = !System.getProperty("os.name").toLowerCase()
				.contains("windows");
	}

	private String id;
	private AtomicBoolean isDone = new AtomicBoolean(false);
	private List<FileInfo> files = Collections
			.synchronizedList(new ArrayList<>());
	private String folder;
	private String searchText;
	private AtomicBoolean stopRequested = new AtomicBoolean(false);

	/**
	 * @return the isDone
	 */
	public boolean isDone() {
		return isDone.get();
	}

	/**
	 * @param isDone the isDone to set
	 */
	public void setDone(boolean isDone) {
		this.isDone.set(isDone);
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

	/**
	 * @return the stopRequested
	 */
	public AtomicBoolean getStopRequested() {
		return stopRequested;
	}

	/**
	 * 
	 */
	public void stop() {
		System.out.println("Stopping...");
		this.stopRequested.set(true);
	}

	@Override
	public void run() {
		try {
			System.out.println("Search start");
			Files.walkFileTree(Paths.get(folder), new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					// System.out.println("Search: " + dir.toString());
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}

					if (isMatch(dir, searchText)) {
						File fdir = dir.toFile();
						FileInfo info = new FileInfo(fdir.getName(),
								fdir.getAbsolutePath(), "", "", "Directory", 0,
								0, null, posix);
						files.add(info);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					// System.out.println("Search: " + file);
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}

					if (isMatch(file, searchText)) {
						File f = file.toFile();
						FileInfo info = new FileInfo(f.getName(),
								f.getAbsolutePath(), "", "", "File", 0, 0, null,
								posix);
						files.add(info);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					// System.out.println("visit failed: " + file);
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					// System.out.println("post visit failed: " + dir);
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}
					return FileVisitResult.CONTINUE;
				}

				private boolean isMatch(Path path, String text) {
					String name = path.toString();
					return name.toLowerCase(Locale.ENGLISH).contains(text);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Search finished");
		isDone.set(true);
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText.toLowerCase(Locale.ENGLISH);
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
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
}
