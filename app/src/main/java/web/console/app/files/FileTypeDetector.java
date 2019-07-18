/**
 * 
 */
package web.console.app.files;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

/**
 * @author subhro
 *
 */
@Component
public class FileTypeDetector {
	/**
	 * 
	 */
	private Tika tika;

	public FileTypeDetector() {
		tika = new Tika();
	}

	public synchronized String getType(File file) {
		try {
			return tika.detect(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "application/octet-stream";
		}
	}

	public synchronized String getTypeByName(String file) {
		return tika.detect(file);
	}

}
