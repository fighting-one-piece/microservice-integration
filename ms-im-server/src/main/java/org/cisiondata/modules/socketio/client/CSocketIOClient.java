package org.cisiondata.modules.socketio.client;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CSocketIOClient {
	
	public static void main(String[] args) throws URISyntaxException {
		IO.Options options = new IO.Options();    
        options.forceNew = true;
        options.reconnection = true;
    	Socket socket = IO.socket("http://localhost:8081?clientid=client1", options);
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			
			@Override
			public void call(Object... arg0) {
				
			}
		}).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
			
			@Override
			public void call(Object... arg0) {
				
			}
		}).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
			
			@Override
			public void call(Object... arg0) {
				
			}
		}).on("cevent", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				String msgContent = (String) args[0];
				System.err.println(msgContent);
			}
		});
		socket.emit("cevent", "");
	}

}
