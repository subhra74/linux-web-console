/**
 * 
 */
package web.console.app.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import web.console.app.files.copy.FileCopyProgressResponse;
import web.console.app.files.copy.FileCopyTask;

/**
 * @author subhro
 *
 */
@Component
public class FileOperations {
	private final Map<String, FileCopyTask> pendingOperations = new ConcurrentHashMap<>();
	private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

	public void setPosixPermission(String file, PosixPermission perm)
			throws Exception {
		Set<PosixFilePermission> permissions = Files
				.getPosixFilePermissions(Paths.get(file));

		if (perm.isExecutable()) {
			permissions.add(PosixFilePermission.OWNER_EXECUTE);
		} else {
			permissions.remove(PosixFilePermission.OWNER_EXECUTE);
		}

		if ("READ_WRITE".equals(perm.getOwnerAccess())) {
			permissions.add(PosixFilePermission.OWNER_READ);
			permissions.add(PosixFilePermission.OWNER_WRITE);
		} else if ("READ_ONLY".equals(perm.getOwnerAccess())) {
			permissions.add(PosixFilePermission.OWNER_READ);
			permissions.remove(PosixFilePermission.OWNER_WRITE);
		} else if ("WRITE_ONLY".equals(perm.getOwnerAccess())) {
			permissions.remove(PosixFilePermission.OWNER_READ);
			permissions.add(PosixFilePermission.OWNER_WRITE);
		}else {
			permissions.remove(PosixFilePermission.OWNER_READ);
			permissions.remove(PosixFilePermission.OWNER_WRITE);
		}

		if ("READ_WRITE".equals(perm.getGroupAccess())) {
			permissions.add(PosixFilePermission.GROUP_READ);
			permissions.add(PosixFilePermission.GROUP_WRITE);
		} else if ("READ_ONLY".equals(perm.getGroupAccess())) {
			permissions.add(PosixFilePermission.GROUP_READ);
			permissions.remove(PosixFilePermission.GROUP_WRITE);
		} else if ("WRITE_ONLY".equals(perm.getGroupAccess())) {
			permissions.remove(PosixFilePermission.GROUP_READ);
			permissions.add(PosixFilePermission.GROUP_WRITE);
		}else {
			permissions.remove(PosixFilePermission.GROUP_READ);
			permissions.remove(PosixFilePermission.GROUP_WRITE);
			permissions.remove(PosixFilePermission.GROUP_EXECUTE);
		}

		if ("READ_WRITE".equals(perm.getOtherAccess())) {
			permissions.add(PosixFilePermission.OTHERS_READ);
			permissions.add(PosixFilePermission.OTHERS_WRITE);
		} else if ("READ_ONLY".equals(perm.getOtherAccess())) {
			permissions.add(PosixFilePermission.OTHERS_READ);
			permissions.remove(PosixFilePermission.OTHERS_WRITE);
		} else if ("WRITE_ONLY".equals(perm.getOtherAccess())) {
			permissions.remove(PosixFilePermission.OTHERS_READ);
			permissions.add(PosixFilePermission.OTHERS_WRITE);
		}else {
			permissions.remove(PosixFilePermission.OTHERS_READ);
			permissions.remove(PosixFilePermission.OTHERS_WRITE);
			permissions.remove(PosixFilePermission.OTHERS_EXECUTE);
		}
		
		Files.setPosixFilePermissions(Paths.get(file), permissions);
	}

	public PosixPermission getPosixPermission(String file) throws Exception {
		Path path = Paths.get(file);
		PosixFileAttributeView view = Files.getFileAttributeView(path,
				PosixFileAttributeView.class);
		PosixFileAttributes attrs = view.readAttributes();
		PosixPermission perm = new PosixPermission();
		perm.setOwner(attrs.owner().getName());
		perm.setGroup(attrs.group().getName());
		Set<PosixFilePermission> permissions = attrs.permissions();
		perm.setExecutable(
				permissions.contains(PosixFilePermission.OWNER_EXECUTE));

		String ownerPerm = "NONE";
		if (permissions.contains(PosixFilePermission.OWNER_READ)
				&& permissions.contains(PosixFilePermission.OWNER_WRITE)) {
			ownerPerm = "READ_WRITE";
		} else if (permissions.contains(PosixFilePermission.OWNER_READ)) {
			ownerPerm = "READ_ONLY";
		} else if (permissions.contains(PosixFilePermission.OWNER_WRITE)) {
			ownerPerm = "WRITE_ONLY";
		}
		perm.setOwnerAccess(ownerPerm);

		String groupPerm = "NONE";
		if (permissions.contains(PosixFilePermission.GROUP_READ)
				&& permissions.contains(PosixFilePermission.GROUP_WRITE)) {
			groupPerm = "READ_WRITE";
		} else if (permissions.contains(PosixFilePermission.GROUP_READ)) {
			groupPerm = "READ_ONLY";
		} else if (permissions.contains(PosixFilePermission.GROUP_WRITE)) {
			groupPerm = "WRITE_ONLY";
		}
		perm.setGroupAccess(groupPerm);

		String otherPerm = "NONE";
		if (permissions.contains(PosixFilePermission.OTHERS_READ)
				&& permissions.contains(PosixFilePermission.OTHERS_WRITE)) {
			otherPerm = "READ_WRITE";
		} else if (permissions.contains(PosixFilePermission.OTHERS_READ)) {
			otherPerm = "READ_ONLY";
		} else if (permissions.contains(PosixFilePermission.OTHERS_WRITE)) {
			otherPerm = "WRITE_ONLY";
		}
		perm.setOtherAccess(otherPerm);
		return perm;
	}

	public void rename(String oldName, String newName, String folder)
			throws IOException {
		Files.move(Paths.get(folder, oldName), Paths.get(folder, newName));
	}

	public void deleteFiles(List<String> files) throws IOException {

		for (String file : files) {
			System.out.println("Delete: " + file);
			File f = new File(file);
			if (f.isDirectory()) {
				File[] children = f.listFiles();
				if (children != null) {
					deleteFiles(Arrays.asList(children).stream()
							.map(a -> a.getAbsolutePath())
							.collect(Collectors.toList()));
				}
			}
			if (!f.delete()) {
				throw new IOException(
						"Unable to delete: " + f.getAbsolutePath());
			}
		}
	}

	/**
	 * @return the pendingOperations
	 */
	public Map<String, FileCopyTask> getPendingOperations() {
		return pendingOperations;
	}

	/**
	 * @param sourceFile
	 * @param destinationFolder
	 * @param move
	 * @return
	 */
	public String createFileCopyTask(List<String> sourceFile,
			String destinationFolder, boolean move) {
		FileCopyTask cp = new FileCopyTask(sourceFile, destinationFolder, move);
		pendingOperations.put(cp.getId(), cp);
		threadPool.submit(cp);
		return cp.getId();
	}

	/**
	 * @param id
	 * @return
	 */
	public List<FileCopyProgressResponse> getProgress(List<String> idList) {
		List<FileCopyProgressResponse> list = new ArrayList<>();
		for (String id : idList) {
			FileCopyTask cp = pendingOperations.get(id);
			FileCopyProgressResponse r = new FileCopyProgressResponse(
					cp.getId(), cp.getName(), (int) cp.getProgress(),
					cp.getStatus(), String.join("\n", cp.getErrorMessage()),
					cp.isHasErrors());
			list.add(r);
		}
		return list;
	}

	public String getOpName(String id) {
		return pendingOperations.get(id).getName();
	}

	public void cancelTask(String id) {
		FileCopyTask cp = pendingOperations.get(id);
		cp.cancel();
	}
}
