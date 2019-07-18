/**
 * 
 */
package web.console.app.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * @author subhro
 *
 */
public class SshPtyProcess {

	private JSch jsch;
	private Session session;
	private String host;
	private String user;
	private int port;
	private String keyFile, passphrase;
	private String password;

	private OutputStream out;
	private ChannelShell shell;
	private InputStream in;

	/**
	 * @param host
	 * @param user
	 * @param port
	 * @param password
	 * @param keyFile
	 * @param passphrase
	 */
	public SshPtyProcess(String host, String user, int port, String password,
			String keyFile, String passphrase) {
		super();
		this.host = host;
		this.user = user;
		this.port = port;
		this.password = password;
		this.keyFile = keyFile;
		this.passphrase = passphrase;
	}

	public void connect() throws Exception {
		MyUserInfo info = new MyUserInfo();
		jsch = new JSch();
		JSch.setConfig("MaxAuthTries", "5");

		if (keyFile != null && keyFile.length() > 0) {
			jsch.addIdentity(keyFile);
		}

		session = jsch.getSession(user, host, port);

		session.setUserInfo(info);

		session.setPassword(info.getPassword());
		// session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications",
				"publickey,keyboard-interactive,password");

		session.connect();

		System.out.println("Client version: " + session.getClientVersion());
		System.out.println("Server host: " + session.getHost());
		System.out.println("Server version: " + session.getServerVersion());
		System.out.println(
				"Hostkey: " + session.getHostKey().getFingerPrint(jsch));

		shell = (ChannelShell) session.openChannel("shell");
		in = shell.getInputStream();
		out = shell.getOutputStream();
		shell.connect();
	}

	class MyUserInfo implements UserInfo {

		@Override
		public String getPassphrase() {
			return passphrase;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public boolean promptPassword(String message) {
			return true;
		}

		@Override
		public boolean promptPassphrase(String message) {
			return true;
		}

		@Override
		public boolean promptYesNo(String message) {
			return true;
		}

		@Override
		public void showMessage(String message) {
			try {
				out.write(message.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @return the jsch
	 */
	public JSch getJsch() {
		return jsch;
	}

	/**
	 * @param jsch the jsch to set
	 */
	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the keyFile
	 */
	public String getKeyFile() {
		return keyFile;
	}

	/**
	 * @param keyFile the keyFile to set
	 */
	public void setKeyFile(String keyFile) {
		this.keyFile = keyFile;
	}

	/**
	 * @return the passphrase
	 */
	public String getPassphrase() {
		return passphrase;
	}

	/**
	 * @param passphrase the passphrase to set
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the out
	 */
	public OutputStream getOut() {
		return out;
	}

	/**
	 * @param out the out to set
	 */
	public void setOut(OutputStream out) {
		this.out = out;
	}

	/**
	 * @return the shell
	 */
	public ChannelShell getShell() {
		return shell;
	}

	/**
	 * @param shell the shell to set
	 */
	public void setShell(ChannelShell shell) {
		this.shell = shell;
	}

	/**
	 * @return the in
	 */
	public InputStream getIn() {
		return in;
	}

	/**
	 * @param in the in to set
	 */
	public void setIn(InputStream in) {
		this.in = in;
	}

	public void close() {
		try {
			shell.disconnect();
		} catch (Exception e) {

		}
		try {
			session.disconnect();
		} catch (Exception e) {

		}

		System.out.println("Ssh shell closed");
	}

	public void waitFor() {
		while (shell.isConnected()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
