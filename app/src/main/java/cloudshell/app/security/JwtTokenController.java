/**
 * 
 */
package cloudshell.app.security;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloudshell.app.Application;
import io.jsonwebtoken.Jwts;

/**
 * @author subhro
 *
 */
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api")
public class JwtTokenController {

	@PostMapping("/token")
	public Map<String, String> generateToken(Principal principal) {
		Map<String, String> map = new HashMap<>();
		String token = Jwts.builder().setSubject(principal.getName())
				.signWith(Application.SECRET_KEY).compact();
		map.put("token", token);
		map.put("posix", (!System.getProperty("os.name").toLowerCase()
				.contains("windows")) + "");
		return map;
	}

	@PostMapping("/auth")
	public void checkToken() {
	}

	@GetMapping("/token/temp")
	public Map<String, String> generateTempToken(Principal principal) {
		Map<String, String> map = new HashMap<>();
		String token = Jwts.builder().setSubject(principal.getName())
				.setExpiration(
						new Date(System.currentTimeMillis() + (60 * 1000)))
				.signWith(Application.SECRET_KEY).compact();
		map.put("token", token);
		return map;
	}
}
