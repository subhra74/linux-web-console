/**
 * 
 */
package cloudshell.app.terminal;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author subhro
 *
 */
public interface PtyProcessPipe {
	public int read(byte[] b) throws Exception;

	public void write(byte[] b, int off, int len) throws Exception;

	public void resizePty(int col, int row, int wp, int hp);
}
