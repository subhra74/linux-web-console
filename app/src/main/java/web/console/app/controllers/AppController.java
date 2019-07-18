/**
 * 
 */
package web.console.app.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import web.console.app.AppContext;
import web.console.app.config.ConfigManager;
import web.console.app.terminal.PtySession;

/**
 * @author subhro
 *
 */
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api")
public class AppController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/app/terminal/{appId}/resize")
	public void resizePty(@PathVariable String appId,
			@RequestBody Map<String, Integer> body) {
		AppContext.INSTANCES.get(appId).resizePty(body.get("row"),
				body.get("col"));
	}

	@PostMapping("/app/terminal")
	public Map<String, String> createTerminal() throws Exception {
		PtySession pty = new PtySession();
		AppContext.INSTANCES.put(pty.getId(), pty);
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", pty.getId());
		return map;
	}

	@GetMapping("/app/config")
	public Map<String, String> getConfig() {
		Map<String, String> map = new HashMap<>();
		map.put("app.default-user", System.getProperty("app.default-user"));
		map.put("app.default-pass", System.getProperty("app.default-pass"));
		map.put("app.default-shell", System.getProperty("app.default-shell"));
		return map;
	}

	@PostMapping("/app/config")
	public void setConfig(@RequestBody Map<String, String> map)
			throws IOException {
		for (String key : map.keySet()) {
			String val = map.get(key);
			if (val != null && val.length() > 0) {
				if (key.equals("app.default-pass")) {
					System.setProperty(key, passwordEncoder.encode(val));
				} else {
					System.setProperty(key, val);
				}
			}
		}
		ConfigManager.saveUserDetails();
	}

}
