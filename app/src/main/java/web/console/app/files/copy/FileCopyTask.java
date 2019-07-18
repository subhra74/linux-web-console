/**
 * 
 */
package web.console.app.files.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author subhro
 *
 */
public class FileCopyTask implements Runnable {
	private long total, copied;
	private List<String> src;
	private String target;
	private boolean move;
	private String status;
	private final byte[] BUFFER = new byte[8192];
	private List<String> errorMessage = new ArrayList<>();
	private boolean hasErrors = false;
	private AtomicBoolean stopRequested = new AtomicBoolean(false);
	private String id;

	/**
	 * @param src
	 * @param target
	 * @param move
	 */
	public FileCopyTask(List<String> src, String target, boolean move) {
		super();
		this.id = UUID.randomUUID().toString();
		this.src = src;
		this.target = target;
		this.move = move;
		this.status = "Waiting...";
	}

	@Override
	public void run() {
		for (String s : src) {
			File f = new File(s);
			calculateSize(f, target);
			if (stopRequested.get()) {
				return;
			}
		}

		for (String s : src) {
			File f = new File(s);
			moveFiles(f, target);
			if (stopRequested.get()) {
				return;
			}
		}

		this.status = "Finished";
	}

	public double getProgress() {
		if (total < 1) {
			return 0;
		}
		return ((double) copied * 100) / total;
	}

	private void calculateSize(File f, String targetFolder) {
		status = "Preparing...";
		if (stopRequested.get()) {
			return;
		}
		String name = f.getName();
		File targetFile = new File(targetFolder, name);

		if (f.isDirectory()) {
			File files[] = null;
			try {
				files = f.listFiles();
				if (stopRequested.get()) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (files == null || files.length < 1) {
				return;
			}
			for (File file : files) {
				calculateSize(file, targetFile.getAbsolutePath());
			}
		} else {
			total += f.length();
		}
	}

	private void moveFiles(File f, String targetFolder) {
		status = move ? "Moving files ..." : "Copying files ...";
		if (stopRequested.get()) {
			return;
		}
		String name = f.getName();
		File targetFile = new File(targetFolder, name);

		if (f.isDirectory()) {
			targetFile.mkdirs();
			File files[] = null;
			try {
				files = f.listFiles();
				if (stopRequested.get()) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (files == null || files.length < 1) {
				return;
			}
			for (File file : files) {
				moveFiles(file, targetFile.getAbsolutePath());
				if (stopRequested.get()) {
					return;
				}
			}
		} else {
			copy(f, targetFile);
		}

		if (move) {
			if (!f.delete()) {
				hasErrors = true;
				errorMessage.add("Error deleting:  " + src + ";");
			}
		}
	}

	private void copy(File src, File target) {
		if (stopRequested.get()) {
			return;
		}

		try (InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(target)) {
			while (!stopRequested.get()) {
				int x = in.read(BUFFER);
				if (x == -1)
					break;
				out.write(BUFFER, 0, x);
				copied += x;
				Thread.sleep(10);
			}
		} catch (Exception e) {
			hasErrors = true;
			e.printStackTrace();
			errorMessage.add("Error copying:  " + src + " to " + target + " - "
					+ e.getMessage());
			return;
		}
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	/**
	 * @return the copied
	 */
	public long getCopied() {
		return copied;
	}

	/**
	 * @param copied the copied to set
	 */
	public void setCopied(long copied) {
		this.copied = copied;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the errorMessage
	 */
	public List<String> getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(List<String> errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the hasErrors
	 */
	public boolean isHasErrors() {
		return hasErrors;
	}

	/**
	 * @param hasErrors the hasErrors to set
	 */
	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	/**
	 * @return the stopRequested
	 */
	public AtomicBoolean getStopRequested() {
		return stopRequested;
	}

	/**
	 * @param stopRequested the stopRequested to set
	 */
	public void setStopRequested(AtomicBoolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return src.size() == 1 ? src.get(0)
				: src.get(0) + (src.size() - 1) + " files";
	}
	
	public void cancel() {
		this.stopRequested.set(true);
	}
}
