package org.platform.modules.socketio.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.platform.modules.socketio.entity.Client;
import org.platform.modules.socketio.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@Component
public class SocketIOServerEventHandler {

	@Autowired
	private SocketIOServer socketIOServer = null;

	private static Map<String, Client> clientInfos = new HashMap<String, Client>();

	@OnConnect
	public void onConnect(SocketIOClient socketIOClient) {
		String clientId = socketIOClient.getHandshakeData().getSingleUrlParam("clientid");
		Client clientInfo = clientInfos.get(clientId);
		if (clientInfo == null) {
			clientInfo = new Client();
			clientInfo.setClientId(clientId);
		} 
		Date nowTime = new Date(System.currentTimeMillis());
		clientInfo.setConnected((short) 1);
		System.err.println(socketIOClient.getSessionId());
		System.err.println(socketIOClient.getSessionId().getMostSignificantBits());
		System.err.println(socketIOClient.getSessionId().getLeastSignificantBits());
		clientInfo.setMostSignBits(socketIOClient.getSessionId().getMostSignificantBits());
		clientInfo.setLeastSignBits(socketIOClient.getSessionId().getLeastSignificantBits());
		clientInfo.setLastConnectedDate(nowTime);
		clientInfos.put(clientId, clientInfo);
	}

	// 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
	@OnDisconnect
	public void onDisconnect(SocketIOClient socketIOClient) {
		String clientId = socketIOClient.getHandshakeData().getSingleUrlParam("clientid");
		Client clientInfo = clientInfos.get(clientId);
		if (clientInfo != null) {
			clientInfo.setConnected((short) 0);
			clientInfo.setMostSignBits(null);
			clientInfo.setLeastSignBits(null);
			clientInfos.put(clientId, clientInfo);
		}
	}

	// 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
	@OnEvent(value = "messageevent")
	public void onEvent(SocketIOClient socketIOClient, Message data, AckRequest request) {
		String targetClientId = data.getTargetClientId();
		Client clientInfo = clientInfos.get(targetClientId);
		if (clientInfo != null && clientInfo.getConnected() != 0) {
			UUID uuid = new UUID(clientInfo.getMostSignBits(), clientInfo.getLeastSignBits());
			System.err.println(clientInfo.getMostSignBits());
			System.err.println(clientInfo.getLeastSignBits());
			System.err.println(uuid.toString());
			Message sendData = new Message();
			sendData.setSourceClientId(data.getSourceClientId());
			sendData.setTargetClientId(data.getTargetClientId());
			sendData.setMsgType("chat");
			sendData.setMsgContent(data.getMsgContent());
			socketIOClient.sendEvent("messageevent", sendData);
			socketIOServer.getClient(uuid).sendEvent("messageevent", sendData);
		}
	}

}
