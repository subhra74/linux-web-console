/**
 * 
 */
package web.console.app.files;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import web.console.app.files.search.SearchResult;
import web.console.app.files.search.SearchTask;

/**
 * @author subhro
 *
 */
@Component
public class SearchOperations {
	private final Map<String, SearchTask> pendingOperations = new ConcurrentHashMap<>();
	private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

	public String createSearchTask(String folder, String searchText) {
		SearchTask task = new SearchTask(folder, searchText);
		pendingOperations.put(task.getId(), task);
		threadPool.submit(task);
		return task.getId();
	}

	public SearchResult getSearchResult(String id, int fileIndex,
			int folderIndex) {
		SearchTask task = pendingOperations.get(id);
		SearchResult res = new SearchResult(task.isDone(), task.getFiles()
				.stream().skip(fileIndex).collect(Collectors.toList()));
		return res;
	}

	public void cancelSearch(String id) {
		SearchTask task = pendingOperations.get(id);
		task.stop();
	}
}
