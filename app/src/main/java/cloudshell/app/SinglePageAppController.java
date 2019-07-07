package cloudshell.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SinglePageAppController {
	@RequestMapping(value = { "/", "/app/**", "/login" })
	public String index() {
		return "/index.html";
	}
}
