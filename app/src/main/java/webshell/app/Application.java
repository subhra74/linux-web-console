package webshell.app;

import java.security.Security;
import java.util.Collections;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import webshell.app.config.ConfigManager;
import webshell.app.security.CustomAuthEntryPoint;
import webshell.app.security.JwtAuthorizationFilter;
import webshell.app.terminal.TerminalWebsocketHandler;

@EnableWebSecurity
@EnableWebSocket
@SpringBootApplication
public class Application extends WebSecurityConfigurerAdapter
		implements WebSocketConfigurer, WebMvcConfigurer, CommandLineRunner {

	@Autowired
	private Environment env;

	static {
		// adds the Bouncy castle provider to java security
		Security.addProvider(new BouncyCastleProvider());
		ConfigManager.checkAndConfigureSSL();
	}

	public static final SecretKey SECRET_KEY = Keys
			.secretKeyFor(SignatureAlgorithm.HS256);

	@Autowired
	private CustomAuthEntryPoint auth;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		System.out.println("Encoded: " + passwordEncoder().encode("admin"));
		System.out.println("Registered ws");
		registry.addHandler(new TerminalWebsocketHandler(), "/term*")
				.setAllowedOrigins("*");// .withSockJS();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().authenticationEntryPoint(auth).and().cors().and()
				.csrf().disable().authorizeRequests().antMatchers("/term**")
				.permitAll().and().authorizeRequests().antMatchers("/bin/**")
				.permitAll().and().authorizeRequests().antMatchers("/api/**")
				.authenticated().and().authorizeRequests().antMatchers("/**")
				.permitAll().and()
				.addFilter(new JwtAuthorizationFilter(authenticationManager()))
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		AuthenticationProvider provider = new AuthenticationProvider() {

			@Override
			public boolean supports(Class<?> authentication) {
				return authentication == (UsernamePasswordAuthenticationToken.class);
			}

			@Override
			public Authentication authenticate(Authentication authentication)
					throws AuthenticationException {
				System.out.println("Authenticating...");
				String user = authentication.getName();
				String pass = authentication.getCredentials().toString();
				System.out.println(
						user + "--" + System.getProperty("app.default-user"));
				if (user.equals(System.getProperty("app.default-user"))
						&& passwordEncoder().matches(pass,
								System.getProperty("app.default-pass"))) {
					return new UsernamePasswordAuthenticationToken(user, pass,
							Collections.emptyList());
				} else {
					throw new BadCredentialsException(
							"External system authentication failed");
				}
			}
		};
		auth.authenticationProvider(provider);
//		auth.authenticationProvider(new UserDetailsService() {
//
//			@Override
//			public UserDetails loadUserByUsername(String username)
//					throws UsernameNotFoundException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});
//		auth.inMemoryAuthentication()
//				.withUser(System.getProperty("app.default-user"))
//				.password("{noop}" + System.getProperty("app.default-pass"))
//				.authorities("ROLE_USER");
	}

	@Override
	public void run(String... args) throws Exception {
		ConfigManager.loadUserDetails(env);
	}
}
