/**
 * 
 */
package web.console.app.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import web.console.app.files.FileOperations;
import web.console.app.files.FileService;
import web.console.app.files.FileTransfer;
import web.console.app.files.PosixPermission;
import web.console.app.files.copy.FileCopyProgressResponse;
import web.console.app.files.copy.FileCopyRequest;

/**
 * @author subhro
 *
 */
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/app")
public class FilesApiController {

	@Autowired
	private FileService service;

	@Autowired
	private FileOperations fs;

	@GetMapping("/folders/home")
	public Map<String, Object> listHome() throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("files", service.list(System.getProperty("user.home")));
		map.put("folder", System.getProperty("user.home"));
		map.put("folderName",
				new File(System.getProperty("user.home")).getName());
		return map;
	}

	@GetMapping("/folders/{path}")
	public Map<String, Object> list(@PathVariable String path)
			throws Exception {
		Map<String, Object> map = new HashMap<>();
		String folder = null;
		if (path == null) {
			folder = System.getProperty("user.home");
		} else {
			folder = new String(Base64.getDecoder().decode(path), "utf-8");
		}
		System.out.println("base64: " + path + " " + folder);
		map.put("files", service.list(folder));
		map.put("folder", folder);
		map.put("folderName", new File(folder).getName());
		return map;
	}

	@GetMapping("/folders/{path}/parent")
	public Map<String, Object> up(@PathVariable String path) throws Exception {
		Map<String, Object> map = new HashMap<>();
		path = new String(Base64.getDecoder().decode(path), "utf-8");
		if (!path.equals("/")) {
			path = new File(path).getParent();
		}
		map.put("files", service.list(path));
		map.put("folder", path);
		map.put("folderName", new File(path).getName());
		return map;
	}

	@PostMapping("/upload/{folder}/{relativePath}")
	public void upload(@PathVariable String folder,
			@PathVariable String relativePath, HttpServletRequest request)
			throws Exception {
		System.out.println("uploading...");
		try {
			folder = new String(Base64.getDecoder().decode(folder), "utf-8");
			relativePath = new String(Base64.getDecoder().decode(relativePath),
					"utf-8");
			FileTransfer fs = new FileTransfer(false);
			fs.transferFile(relativePath, folder, request.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@GetMapping("/folder/tree/home")
	public List<Map<String, ?>> listTreeHome() {
		List<Map<String, ?>> list = new ArrayList<>();
		File f = new File(System.getProperty("user.home"));
		File[] files = f.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (!file.isDirectory()) {
					continue;
				}
				Map<String, Object> entry = new HashMap<>();
				entry.put("name", file.getName());
				entry.put("path", file.getAbsolutePath());
				entry.put("leafNode", !file.isDirectory());
				list.add(entry);
			}
		}
		return list;
	}

	@GetMapping("/folder/tree/path/{encodedPath}")
	public List<Map<String, ?>> listTreePath(@PathVariable String encodedPath)
			throws Exception {
		String path = new String(Base64.getDecoder().decode(encodedPath),
				"utf-8");
		List<Map<String, ?>> list = new ArrayList<>();
		File f = new File(path);
		File[] files = f.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					Map<String, Object> entry = new HashMap<>();
					entry.put("name", file.getName());
					entry.put("path", file.getAbsolutePath());
					entry.put("leafNode", !file.isDirectory());
					list.add(entry);
				}
			}
		}
		return list;
	}

	@GetMapping("/folder/tree/fs")
	public List<Map<String, ?>> listFsRoots() throws Exception {
		List<Map<String, ?>> list = new ArrayList<>();
		File[] files = File.listRoots();
		if (files != null && files.length > 0) {
			for (File file : files) {
				Map<String, Object> entry = new HashMap<>();
				System.out.println("Root: " + file.getName());
				entry.put("name", file.getAbsolutePath());
				entry.put("path", file.getAbsolutePath());
				entry.put("leafNode", !file.isDirectory());
				list.add(entry);
			}
		}
		return list;
	}

	@PostMapping("/fs/{mode}")
	public Map<String, String> startCopyOrMove(@PathVariable String mode,
			@RequestBody FileCopyRequest request) {
		List<String> sourceFile = request.getSourceFile();
		String targetFolder = request.getTargetFolder();
		Map<String, String> map = new HashMap<>();
		String id = fs.createFileCopyTask(sourceFile, targetFolder,
				"move".equals(mode));
		map.put("id", id);
		map.put("name", fs.getOpName(id));
		return map;
	}

	@PostMapping("/fs/progress")
	public List<FileCopyProgressResponse> getProgress(
			@RequestBody List<String> idList) {
		return fs.getProgress(idList);
	}

	@PostMapping("/fs/cancel/{id}")
	public void cancel(@PathVariable String id) {
		fs.cancelTask(id);
	}

	@PostMapping("/fs/rename")
	public void rename(@RequestBody Map<String, String> body)
			throws IOException {
		fs.rename(body.get("oldName"), body.get("newName"), body.get("folder"));
	}

	@DeleteMapping("/fs/delete")
	public void delete(@RequestBody List<String> body) throws IOException {
		fs.deleteFiles(body);
	}

	@GetMapping("/fs/files/{encodedPath}")
	public String getText(@PathVariable String encodedPath) throws Exception {
		return service
				.getText(new String(Base64.getDecoder().decode(encodedPath)));
	}

	@PostMapping("/fs/files/{encodedPath}")
	public void setText(@PathVariable String encodedPath,
			@RequestBody String body) throws Exception {
		service.setText(new String(Base64.getDecoder().decode(encodedPath)),
				body);
	}

	@PostMapping("/fs/mkdir")
	public void mkdir(@RequestBody Map<String, String> map) throws Exception {
		String dir = map.get("dir");
		String name = map.get("name");
		Path path = Paths.get(dir, name);
		Files.createDirectories(path);
	}

	@PostMapping("/fs/touch")
	public void touch(@RequestBody Map<String, String> map) throws Exception {
		String dir = map.get("dir");
		String name = map.get("name");
		Path path = Paths.get(dir, name);
		Files.createFile(path);
	}

	@GetMapping("/app/fs/posix/{encodedPath}")
	public PosixPermission getPosixPerm(@PathVariable String encodedPath)
			throws Exception {
		return this.fs.getPosixPermission(
				new String(Base64.getDecoder().decode(encodedPath), "utf-8"));
	}

	@PostMapping("/app/fs/posix/{encodedPath}")
	public void setPosixPerm(@PathVariable String encodedPath,
			@RequestBody PosixPermission perm) throws Exception {
		this.fs.setPosixPermission(
				new String(Base64.getDecoder().decode(encodedPath), "utf-8"),
				perm);
	}

}
