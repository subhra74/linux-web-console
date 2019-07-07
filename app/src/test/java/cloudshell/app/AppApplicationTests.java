package cloudshell.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationTests {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Test
	public void contextLoads() {
		System.out.println("Password match: " + passwordEncoder.matches("Starscream@64",
				"$2a$10$greBvSdJwMfmrz7Fof0mB.i2oiBNypVeGa9KCBOZ2BPMxXBa3xJUK"));

	}

}
