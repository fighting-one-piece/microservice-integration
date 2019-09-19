package org.platform.modules.websocket.service;

import javax.websocket.Session;

public interface IWebSocketService {
	
	public void openSession(String clientId, Session session);
	
	public void closeSession(String clientId, Session session);
	
	public void sendMessage(String destClientId, String message);
	
	public void sendBroadcastMessage(String srcClientId, String message);

}
