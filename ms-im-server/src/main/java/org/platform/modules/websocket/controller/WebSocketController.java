package org.platform.modules.websocket.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.platform.modules.websocket.service.IWebSocketService;
import org.platform.utils.json.GsonUtils;
import org.platform.utils.spring.SpringBeanFactory;
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
    
    private IWebSocketService webSocketService = SpringBeanFactory.getBean(IWebSocketService.class);
    
    @OnOpen
    public void onOpen(@PathParam("clientId") String clientId, Session session) {
        LOG.info("open connect session {} ", clientId);
        webSocketService.openSession(clientId, session);
    }

    @OnMessage
    public void onMessage(@PathParam("clientId") String clientId, Session session, String message) {
    	LOG.info("receive client {} message {}", clientId, message);
    	webSocketService.sendBroadcastMessage(clientId, message);
    }

    @OnClose
    public void onClose(@PathParam("clientId") String clientId, Session session) {
        LOG.info("close connect session {}", clientId);
        webSocketService.closeSession(clientId, session);
    }

    @OnError
    public void onError(@PathParam("clientId") String clientId, Session session, Throwable error) {
    	LOG.info("error connect session {}", clientId);
        LOG.error(error.getMessage(), error);
    }

    @ResponseBody
	@RequestMapping(value = "/websocket/push/{destClientId}", method = RequestMethod.POST)
	public Object pushMessage(@PathVariable String destClientId, String message) {  
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
    	webSocketService.sendMessage(destClientId, GsonUtils.fromMapToJson(resultMessage));
		return "SUCCESS";
	} 
    
}


