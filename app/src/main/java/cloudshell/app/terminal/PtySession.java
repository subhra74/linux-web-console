/**
 * 
 */
package cloudshell.app.terminal;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import com.pty4j.WinSize;

/**
 * @author subhro
 *
 */
public class PtySession implements Runnable {
	private String id;
	private WebSocketSession ws;
	private Thread t;
	private PtyProcess pty;
	private OutputStream out;
	private InputStream os;
	private boolean ptyAllowed;
	private SshPtyProcess proc;

	/**
	 * 
	 */
	public PtySession() throws Exception {
		id = UUID.randomUUID().toString();

		ptyAllowed = true;
		String[] cmd = "auto".equals(System.getProperty("app.default-shell"))
				? System.getProperty("os.name").toLowerCase()
						.contains("windows") ? new String[] { "cmd" }
								: new String[] { "/bin/bash", "-l" }
				: new String[] { System.getProperty("app.default-shell") };

		try {
			// The initial environment to pass to the PTY child process...
			Map<String, String> env = new HashMap<>();
			env.put("TERM", "xterm");
			PtyProcessBuilder pb = new PtyProcessBuilder(cmd);
			pb.setRedirectErrorStream(true);
			pb.setEnvironment(env);
			pty = pb.start();
			os = pty.getInputStream();
			out = pty.getOutputStream();
			System.out.println("Pty created");
		} catch (Exception e) {
			e.printStackTrace();
			if ("true".equals(System.getProperty("app.fallback-local-ssh"))) {
				proc = new SshPtyProcess(
						System.getProperty("app.local-ssh-host"),
						System.getProperty("app.local-ssh-user"),
						Integer.parseInt(
								System.getProperty("app.local-ssh-port")),
						System.getProperty("app.local-ssh-password"),
						System.getProperty("app.local-ssh-keyfile"),
						System.getProperty("app.local-ssh-passphrase"));
				proc.connect();
				os = proc.getIn();
				out = proc.getOut();
			}
		}

//		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//			ptyAllowed = false;
//			ProcessBuilder pb = new ProcessBuilder("powershell");
//			pb.redirectErrorStream(true);
//			proc = pb.start();
//			os = proc.getInputStream();
//			out = proc.getOutputStream();
//			System.out.println("Process created");
//			windows = true;
//		} else {
//			ptyAllowed = true;
//			String[] cmd = { "/bin/bash", "-l" };
//			// The initial environment to pass to the PTY child process...
//			Map<String, String> env = new HashMap<>();
//			env.put("TERM", "xterm");
//			PtyProcessBuilder pb = new PtyProcessBuilder(cmd);
//			pb.setRedirectErrorStream(true);
//			pb.setEnvironment(env);
//			pty = pb.start();
//			os = pty.getInputStream();
//			out = pty.getOutputStream();
//			System.out.println("Pty created");
//			windows = false;
//		}
	}

	public void start() {
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			byte[] buf = new byte[512];
			try {
				while (true) {
					int x = os.read(buf);
					if (x == -1) {
						System.out.println("Stream end");
						break;
					}
					ws.sendMessage(
							new TextMessage(new String(buf, 0, x, "utf-8")));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (ptyAllowed && pty != null) {
				System.out.println("Waiting for pty...");
				pty.waitFor();
			} else if (proc != null) {
				System.out.println("Waiting fot proc...");
				proc.waitFor();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pty != null) {
				pty.destroy();
			}
			if (proc != null) {
				proc.close();
			}
		}
		System.out.println("Thread finished");
	}

	public void sendData(String text) {
		try {
//			if (windows) {
//				ws.sendMessage(new TextMessage(text));
//				if (text.equals("\r")) {
//					System.out.println("replaced");
//					text = "\r\n";
//				}
//				out.write(text.getBytes("utf-8"));
//				out.flush();
//			} else {
//				out.write(text.getBytes("utf-8"));
//				out.flush();
//			}

//			if (text.equals("\n")) {
//				out.write("\r\n".getBytes());
//			} else {
//				System.out.println("sending to text: " + text + " len: "
//						+ text.length() + " char " + (int) text.charAt(0)
//						+ " - " + (int) '\n');
//				out.write(text.getBytes());// text.getBytes("utf-8"));
//				out.flush();
//			}

			out.write(text.getBytes("utf-8"));
			out.flush();

			System.out.println("sent text");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (pty != null) {
			pty.destroyForcibly();
		}
		if (proc != null) {
			proc.close();
		}
	}

	public void resizePty(int row, int col) {
		if (pty != null) {
			pty.setWinSize(new WinSize(col, row));
		}
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

	/**
	 * @return the ws
	 */
	public WebSocketSession getWs() {
		return ws;
	}

	/**
	 * @param ws the ws to set
	 */
	public void setWs(WebSocketSession ws) {
		this.ws = ws;
	}

	/**
	 * @return the t
	 */
	public Thread getT() {
		return t;
	}

	/**
	 * @param t the t to set
	 */
	public void setT(Thread t) {
		this.t = t;
	}

	/**
	 * @return the pty
	 */
	public PtyProcess getPty() {
		return pty;
	}

	/**
	 * @param pty the pty to set
	 */
	public void setPty(PtyProcess pty) {
		this.pty = pty;
	}
}
