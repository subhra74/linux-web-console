/**
 * 
 */
package web.console.app.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import web.console.app.files.FileService;
import web.console.app.files.search.SearchResult;

/**
 * @author subhro
 *
 */
@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/api/app/fs/search")
public class SearchApiController {

	@Autowired
	private FileService service;

	@PostMapping("/")
	public Map<String, String> initSearch(
			@RequestBody Map<String, String> request) {
		String id = service.createSearch(request.get("folder"),
				request.get("searchText"));
		Map<String, String> response = new HashMap<String, String>();
		response.put("id", id);
		return response;
	}

	@DeleteMapping("/{id}")
	public void cancelSearch(@PathVariable String id) {
		this.service.cancelSearch(id);
	}

	@GetMapping("/{id}")
	public SearchResult getSearchResult(@PathVariable String id,
			@RequestParam(defaultValue = "0", required = false) int fileIndex,
			@RequestParam(defaultValue = "0", required = false) int folderIndex) {
		return this.service.getSearchResult(id, fileIndex, folderIndex);
	}

}
