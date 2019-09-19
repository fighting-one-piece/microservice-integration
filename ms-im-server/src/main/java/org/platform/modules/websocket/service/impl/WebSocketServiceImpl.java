package org.platform.modules.websocket.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.platform.modules.websocket.service.IWebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("webSocketService")
public class WebSocketServiceImpl implements IWebSocketService {

	private Logger LOG = LoggerFactory.getLogger(WebSocketServiceImpl.class);

	public static Map<String, Session> SESSIONS = new ConcurrentHashMap<String, Session>();

	@Override
	public void openSession(String clientId, Session session) {
		SESSIONS.put(clientId, session);
		LOG.info("open connect session size {}", SESSIONS.size());
		sendMessage(clientId, "open connect session success");
	}
	
	@Override
	public void closeSession(String clientId, Session session) {
		SESSIONS.remove(clientId);
        LOG.info("close connect session size", SESSIONS.size());
	}

	@Override
	public void sendMessage(String destClientId, String message) {
		if (!SESSIONS.containsKey(destClientId)) return;
		Session session = SESSIONS.get(destClientId);
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendBroadcastMessage(String srcClientId, String message) {
		for (Map.Entry<String, Session> entry : SESSIONS.entrySet()) {
			String currentClientId = entry.getKey();
			if (srcClientId.equals(currentClientId)) continue;
			try {
				entry.getValue().getBasicRemote().sendText(message);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
