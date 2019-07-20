/**
 * 
 */
package web.console.app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import web.console.app.health.ProcessInfo;
import web.console.app.health.SystemHealthMonitor;
import web.console.app.health.SystemStats;

/**
 * @author subhro
 *
 */
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/app")
public class HealthApiController {

	@Autowired
	private SystemHealthMonitor healthMon;

	@GetMapping("/sys/stats")
	public SystemStats getStats() {
		return this.healthMon.getStats();
	}

	@GetMapping("/sys/procs")
	public List<ProcessInfo> getProcessList() {
		return this.healthMon.getProcessList();
	}

	@PostMapping("/sys/procs")
	public Map<String, Boolean> killProcesses(
			@RequestBody List<Integer> pidList) {
		Map<String, Boolean> map = new HashMap<>();
		map.put("success", this.healthMon.killProcess(pidList));
		return map;
	}

}
