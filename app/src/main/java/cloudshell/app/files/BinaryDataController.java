/**
 * 
 */
package cloudshell.app.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloudshell.app.Application;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * @author subhro
 *
 */
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/bin")
public class BinaryDataController {

	@Autowired
	private FileTypeDetector typeDetector;

	private void validateToken(String token) throws Exception {
		Jws<Claims> parsedToken = Jwts.parser()
				.setSigningKey(Application.SECRET_KEY).parseClaimsJws(token);

		String username = parsedToken.getBody().getSubject();

		if (username != null && username.length() > 0
				&& username.equals(System.getProperty("app.default-user"))) {
			System.out.println("Token and user is valid");
			return;
		}

		throw new Exception("Invalid username: " + username);
	}

	@GetMapping("/image/{encodedPath}")
	public ResponseEntity<byte[]> getImage(@PathVariable String encodedPath,
			@RequestParam String token) throws Exception {
		validateToken(token);
		String path = new String(Base64.getDecoder().decode(encodedPath));
		byte[] b = Files.readAllBytes(Paths.get(path));
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add("Content-Type", getImageType(path));
		ResponseEntity<byte[]> resp = new ResponseEntity<byte[]>(b, headers,
				HttpStatus.OK);
		return resp;
	}

	@GetMapping("/blob/{encodedPath}")
	public void getBlob(@PathVariable String encodedPath,
			@RequestParam String token, HttpServletRequest request,
			HttpServletResponse resp) throws Exception {
		validateToken(token);
		String path = new String(Base64.getDecoder().decode(encodedPath));
		System.out.println("Get file: " + path);
		long fileSize = Files.size(Paths.get(path));
		String r = request.getHeader("Range");
		long upperBound = fileSize - 1;
		long lowerBound = 0;
		if (r != null) {
			System.out.println(r);
			String rstr = r.split("=")[1];
			String arr[] = rstr.split("-");
			lowerBound = Long.parseLong(arr[0]);
			if (arr.length > 1 && arr[1].length() > 0) {
				upperBound = Long.parseLong(arr[1]);
			}
		}

		System.out.println("lb: " + lowerBound + " ub: " + fileSize);

		if (r != null) {
			resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		}
		resp.addHeader("Content-Length", (fileSize - lowerBound) + "");
		resp.addHeader("Content-Range",
				"bytes " + lowerBound + "-" + upperBound + "/" + fileSize);// (fileSize
																			// -
																			// lowerBound)
																			// +
																			// "");
		resp.addHeader("Content-Type", typeDetector.getType(new File(path)));
		long rem = fileSize - lowerBound;
		try (InputStream in = new FileInputStream(path)) {
			in.skip(lowerBound);
			byte[] b = new byte[8192];
			while (rem > 0) {
				int x = in.read(b, 0, (int) (rem > b.length ? b.length : rem));
				if (x == -1)
					break;
				resp.getOutputStream().write(b, 0, x);
				rem -= x;
			}
		}
		// Files.copy(Paths.get(path), resp.getOutputStream());
	}

	@GetMapping("/download")
	public void downloadFiles(
			@RequestParam(name = "folder") String encodedFolder,
			@RequestParam(name = "files", required = false) String encodedFiles,
			@RequestParam String token, HttpServletResponse response)
			throws Exception {
		validateToken(token);
		int fileCount = 0;

		String folder = new String(Base64.getDecoder().decode(encodedFolder),
				"utf-8");

		List<String> fileList = new ArrayList<>();

		if (encodedFiles != null) {
			String files = new String(Base64.getDecoder().decode(encodedFiles),
					"utf-8");
			String[] arr1 = files.split("/");
			if (arr1 != null && arr1.length > 0) {
				fileCount = arr1.length;
				for (String str : arr1) {
					fileList.add(new File(folder, str).getAbsolutePath());
				}
			}
		}

		if (fileList.size() == 0) {
			throw new IOException();
		}

		String name = "download.zip";

		boolean compress = false;

		if (fileCount == 1) {
			File f = new File(fileList.get(0));
			name = f.getName();
			compress = f.isDirectory();
			if (name.length() < 1) {
				name = "files";
			}
		} else {
			compress = true;
		}

		response.addHeader("Content-Disposition",
				"attachment; filename=\"" + name + ".zip" + "\"");
		response.setContentType("application/octet-stream");
		FileTransfer fs = new FileTransfer(compress);
		fs.transferFiles(fileList, response.getOutputStream());
	}

	/**
	 * @param path
	 * @return
	 */
	private String getImageType(String path) {
		return typeDetector.getTypeByName(path);
	}
}
