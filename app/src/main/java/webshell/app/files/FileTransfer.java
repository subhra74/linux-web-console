/**
 * 
 */
package webshell.app.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author subhro
 *
 */
public class FileTransfer {

	private final byte[] BUFFER = new byte[8192];
	private boolean compress;

	/**
	 * 
	 */
	public FileTransfer(boolean compress) {
		this.compress = compress;
	}

	public void transferFile(String relativePath, String folder,
			InputStream in) {
		File f = new File(folder, relativePath);
		File parent=f.getParentFile();
		parent.mkdirs();
		System.out.println("Creating folder: "+parent.getAbsolutePath());
		System.out.println("Creating file: "+f.getAbsolutePath());
		try (FileOutputStream out = new FileOutputStream(f)) {
			copyStream(in, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transferFiles(List<String> files, OutputStream out) {
		if (!compress) {
			try (InputStream in = new FileInputStream(files.get(0))) {
				copyStream(in, out);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try (ZipOutputStream zout = new ZipOutputStream(out)) {
				for (String file : files) {
					File f = new File(file);
					if (f.isDirectory()) {
						if (!walkTree(f, zout, f.getName())) {
							return;
						}
					} else {
						boolean writing = false;
						try (InputStream in = new FileInputStream(f)) {
							ZipEntry ze = new ZipEntry(f.getName());
							ze.setSize(f.length());
							writing = true;
							zout.putNextEntry(ze);
							copyStream(in, zout);
							zout.closeEntry();
							writing = false;
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (writing) {
							return;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean walkTree(File folder, ZipOutputStream zout,
			String relativePath) {
		System.out.println(
				"Walking: " + folder.getAbsolutePath() + " - " + relativePath);
		try {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						if (!walkTree(f, zout, combine(relativePath,
								f.getName(), File.separator))) {
							return false;
						}
					} else {
						boolean writing = false;
						try (InputStream in = new FileInputStream(f)) {
							ZipEntry ze = new ZipEntry(combine(relativePath,
									f.getName(), File.separator));
							ze.setSize(f.length());
							writing = true;
							zout.putNextEntry(ze);
							copyStream(in, zout);
							zout.closeEntry();
							writing = false;
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (writing) {
							throw new Exception("error reading file: "
									+ f.getAbsolutePath());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void copyStream(InputStream in, OutputStream out)
			throws IOException {
		while (true) {
			int x = in.read(BUFFER);
			if (x == -1)
				break;
			out.write(BUFFER, 0, x);
		}
	}

	public String combine(String path1, String path2, String separator) {
		if (path2.startsWith(separator)) {
			path2 = path2.substring(1);
		}
		if (!path1.endsWith(separator)) {
			return path1 + separator + path2;
		} else {
			return path1 + path2;
		}
	}
}
