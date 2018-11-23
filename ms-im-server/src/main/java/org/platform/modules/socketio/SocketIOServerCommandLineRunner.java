package org.platform.modules.socketio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOServer;

@Component
public class SocketIOServerCommandLineRunner implements CommandLineRunner {
	
	@Autowired  
    private SocketIOServer socketIOServer = null;  
  
    @Override  
    public void run(String... args) throws Exception {  
        socketIOServer.start();  
    }  
    
}
