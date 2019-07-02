/**
 * 
 */
package webshell.app.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import webshell.app.Application;

/**
 * @author subhro
 *
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	/**
	 * @param authenticationManager
	 */
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		UsernamePasswordAuthenticationToken authentication = getAuthentication(
				request);
		if (authentication == null) {
			filterChain.doFilter(request, response);
			return;
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(
			HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.length() > 0 && token.startsWith("Bearer")) {
			try {
				Jws<Claims> parsedToken = Jwts.parser()
						.setSigningKey(Application.SECRET_KEY)
						.parseClaimsJws(token.replace("Bearer ", ""));

				String username = parsedToken.getBody().getSubject();

				if (username != null && username.length() > 0) {
					return new UsernamePasswordAuthenticationToken(System.getProperty("app.default-user"),
							null, Collections.emptyList());
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		return null;
	}

}
