///**
// * 
// */
//package web.console.app.terminal;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PipedInputStream;
//import java.io.PipedOutputStream;
//import java.io.PipedReader;
//import java.io.PipedWriter;
//import java.io.Reader;
//import java.io.UnsupportedEncodingException;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import com.jcraft.jsch.ChannelShell;
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.Session;
//import com.jcraft.jsch.UserInfo;
//
///**
// * @author subhro
// *
// */
//public class SshPtyProcessPipe implements PtyProcessPipe, UserInfo {
//
//	private PipedInputStream _in, in;
//	private PipedOutputStream _out, out;
//	private String hostName, keyFile, user;
//	private int port;
//	private JSch jsch;
//	private Session session;
//	private ChannelShell shell;
//
//	/**
//	 * @throws IOException
//	 * @throws UnsupportedEncodingException
//	 * 
//	 */
//	public SshPtyProcessPipe() {
//		hostName = "192.168.56.106";
//		port = 22;
//		user = "subhro";
//	}
//
//	public void start() {
//		try {
//			_out = new PipedOutputStream();
//			in = new PipedInputStream(_out, 1);
//			_in = new PipedInputStream(1);
//			out = new PipedOutputStream(_in);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		new Thread(() -> {
//			try {
//				_start();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}).start();
//	}
//
//	public void _start() throws Exception {
//
//		jsch = new JSch();
//		JSch.setConfig("MaxAuthTries", "5");
//
//		if (keyFile != null && keyFile.length() > 0) {
//			jsch.addIdentity(keyFile);
//		}
//
//		session = jsch.getSession(user, hostName, port);
//
//		session.setUserInfo(this);
//		// session.setConfig("StrictHostKeyChecking", "no");
//		session.setConfig("PreferredAuthentications",
//				"publickey,keyboard-interactive,password");
//
//		System.out.println("Before connect");
//
//		session.connect();
//
//		System.out.println("Client version: " + session.getClientVersion());
//		System.out.println("Server host: " + session.getHost());
//		System.out.println("Server version: " + session.getServerVersion());
//		System.out.println(
//				"Hostkey: " + session.getHostKey().getFingerPrint(jsch));
//
//		shell = (ChannelShell) session.openChannel("shell");
//		shell.setEnv("TERM", "xterm");
//
//		shell.setInputStream(in);
//		shell.setOutputStream(out);
//
//		shell.connect();
//	}
//
//	public void resizePty(int col, int row, int wp, int hp) {
//		shell.setPtySize(col, row, wp, hp);
//	}
//
//	private String readLine() throws IOException {
//		System.out.println("Attempt readline");
//		StringBuilder sb = new StringBuilder();
//		while (true) {
//			int ch = in.read();
//			System.out.println("char: " + ch);
//			if (ch == -1 || ch == '\n'|| ch == '\r')
//				break;
//			sb.append((char) ch);
//		}
//		return sb.toString();
//	}
//
//	@Override
//	public String getPassphrase() {
//		try {
//			return readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@Override
//	public String getPassword() {
//		try {
//			String pass = readLine();
//			System.out.println("Paww: " + pass);
//			return pass;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@Override
//	public boolean promptPassword(String message) {
//		System.out.println("prompt password: " + message);
//		try {
//			out.write(message.getBytes("utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return true;
//	}
//
//	@Override
//	public boolean promptPassphrase(String message) {
//		try {
//			out.write(message.getBytes("utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return true;
//	}
//
//	@Override
//	public boolean promptYesNo(String message) {
//		return true;
//	}
//
//	@Override
//	public void showMessage(String message) {
//		System.out.println("prompt messae: " + message);
//		try {
//			out.write(message.getBytes("utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public int read(byte[] b) throws Exception {
//		return _in.read(b);
//	}
//
//	@Override
//	public void write(byte[] b, int off, int len) throws Exception {
//		this._out.write(b, off, len);
//	}
//
//}
