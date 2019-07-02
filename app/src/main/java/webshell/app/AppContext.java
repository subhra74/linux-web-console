/**
 * 
 */
package webshell.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import webshell.app.terminal.PtySession;

/**
 * @author subhro
 *
 */

public class AppContext {
	public static final Map<String, PtySession> INSTANCES = new ConcurrentHashMap<>();
	public static final Map<String, PtySession> SESSION_MAP = new ConcurrentHashMap<>();
}
