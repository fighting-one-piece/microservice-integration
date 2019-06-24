package org.platform.modules.websocket.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ServerEndpoint("/websocket/{clientId}")
public class WebSocketController {

    private final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);

    public static Map<String, Session> SESSIONS = new ConcurrentHashMap<String, Session>();
    
    private String clientId = null;

    @OnOpen
    public void onOpen(Session session, @PathParam("clientId") String clientId) {
        LOG.info("establish connect {} ", clientId);
        this.clientId = clientId;
        SESSIONS.put(clientId, session);
        LOG.info("establish connect session size {}", SESSIONS.size());
        send(clientId, "establish connect success");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
    	LOG.info("receive {} message {}", this.clientId, message);
        sendBroadcast(message);
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("close connection");
        SESSIONS.remove(this.clientId);
        LOG.info("close connection session size", SESSIONS.size());
    }

    private void send(String clientId, String message){
    	if (!SESSIONS.containsKey(clientId)) return;
        Session session = SESSIONS.get(clientId);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    private void sendBroadcast(String message){
    	for (Map.Entry<String, Session> entry : SESSIONS.entrySet()) {
    		String targetClientId = entry.getKey();
    		if (this.clientId.equals(targetClientId)) continue;
    		try {
    			entry.getValue().getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
    	}
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error(error.getMessage(), error);
    }

    @ResponseBody
	@RequestMapping(value = "/websocket/push/{clientId}", method = RequestMethod.GET)
	public Object pushToWeb(@PathVariable String clientId, String message) {  
    	Map<String, Object> resultMessage = new HashMap<String, Object>();
    	resultMessage.put("mt", "si1");
    	Map<String, Object> messageContent = new HashMap<String, Object>();
    	messageContent.put("n1", "n1_value");
    	List<Object> n2 = new ArrayList<Object>();
    	n2.add("n2_value_1");
    	n2.add("n2_value_2");
    	messageContent.put("n2", n2);
    	messageContent.put("n3", message);
    	resultMessage.put("mc", messageContent);
    	send(clientId, GsonUtils.fromMapToJson(resultMessage));
		return "SUCCESS";
	} 
    
}


