/**
 * 
 */
package webshell.app.files.search;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author subhro
 *
 */
public class SearchTask implements Runnable {
	/**
	 * @param folder
	 * @param searchText
	 */
	public SearchTask(String folder, String searchText) {
		super();
		this.id = UUID.randomUUID().toString();
		this.folder = folder;
		this.searchText = searchText.toLowerCase(Locale.ENGLISH);
	}

	private String id;
	private AtomicBoolean isDone = new AtomicBoolean(false);
	private List<String> files = new ArrayList<>(), folders = new ArrayList<>();
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
					//System.out.println("Search: " + dir.toString());
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}

					if (isMatch(dir, searchText)) {
						folders.add(dir.toAbsolutePath().toString());
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					//System.out.println("Search: " + file);
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}

					if (isMatch(file, searchText)) {
						files.add(file.toAbsolutePath().toString());
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					//System.out.println("visit failed: " + file);
					if (stopRequested.get()) {
						return FileVisitResult.TERMINATE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					//System.out.println("post visit failed: " + dir);
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
