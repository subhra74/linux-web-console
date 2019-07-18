/**
 * 
 */
package web.console.app.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author subhro
 *
 */
public class WebshellUserDetailsService implements UserDetailsService {
	Properties users = new Properties();

	/**
	 * 
	 */
	public WebshellUserDetailsService() {
		loadProperties();
	}

	private void loadProperties() {
		users.clear();
		String propertyPath = System.getenv("easy-web-shell.config-path");
		if (propertyPath == null) {
			propertyPath = System.getProperty("user.home");
		}

		File f = new File(propertyPath, ".users");

		if (f.exists()) {
			try (InputStream in = new FileInputStream(f)) {
				users.load(in);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (users.size() < 1) {
			users.put("admin", "admin");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		if (!users.containsKey(username)) {
			return null;
		}
		String password = users.getProperty(username);
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return new User(username, password, authorities);
	}

}
