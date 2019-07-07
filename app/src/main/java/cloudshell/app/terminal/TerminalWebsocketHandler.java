package cloudshell.app.terminal;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import cloudshell.app.AppContext;
import cloudshell.app.Application;
import io.jsonwebtoken.Jwts;

/**
 * @author subhro
 *
 */
public class TerminalWebsocketHandler extends AbstractWebSocketHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(TerminalWebsocketHandler.class);

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		logger.info("Incoming session: " + session.getId() + " url: "
				+ session.getUri());

		synchronized (this) {
			URI uri = session.getUri();
			MultiValueMap<String, String> params = UriComponentsBuilder
					.fromUri(uri).build().getQueryParams();

			try {
				String token = params.getFirst("token");
				Jwts.parser().setSigningKey(Application.SECRET_KEY)
						.parseClaimsJws(token);
			} catch (Exception exception) {
				exception.printStackTrace();
				session.close(CloseStatus.BAD_DATA);
				return;
			}

			String appId = params.getFirst("id");
			PtySession app = AppContext.INSTANCES.get(appId);
			if (app == null) {
				logger.error("No instance for session id: " + session.getId()
						+ " data: " + uri);
				session.close(CloseStatus.BAD_DATA);
				return;
			}
			app.setWs(session);
			AppContext.SESSION_MAP.put(session.getId(), app);
			app.start();
		}

		logger.info("Handshake complete session: " + session.getId() + " url: "
				+ session.getUri());

	}

	@Override
	protected void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		System.out.println("Message received: " + message.getPayload());

		PtySession app = AppContext.SESSION_MAP.get(session.getId());

		if (app == null) {
			logger.error("Invalid app id: " + session.getId());
			session.close(CloseStatus.BAD_DATA);
			return;
		}

		app.sendData(message.getPayload());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		System.out.println("Session closed: " + session.getId());
		PtySession app = AppContext.SESSION_MAP.get(session.getId());
		if (app != null) {
			app.close();
			AppContext.SESSION_MAP.remove(session.getId());
			logger.info("Session closed");
		}
	}
}
