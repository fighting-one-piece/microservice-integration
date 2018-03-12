package org.cisiondata.modules.bootstrap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;

@org.springframework.context.annotation.Configuration
public class SocketIOConfiguration {

	@Value("${socketio.server.host}")
	private String host = null;

	@Value("${socketio.server.port}")
	private Integer port = null;
	
	@Value("${socketio.server.maxHttpContentLength}")
	private Integer maxHttpContentLength = null;
	
	@Value("${socketio.server.maxFramePayloadLength}")
	private Integer maxFramePayloadLength = null;

	@Bean
	public SocketIOServer socketIOServer() {
		Configuration configuration = new Configuration();
		configuration.setHostname(host);
		configuration.setPort(port);
		configuration.setMaxHttpContentLength(maxHttpContentLength);
		configuration.setMaxFramePayloadLength(maxFramePayloadLength);
		configuration.setAuthorizationListener(new AuthorizationListener() {
			@Override
			public boolean isAuthorized(HandshakeData data) {
				// http://localhost:8081?username=test&password=test
				// 例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
				// String username = data.getSingleUrlParam("username");
				// String password = data.getSingleUrlParam("password");
				return true;
			}
		});
		SocketIOServer socketIOServer = new SocketIOServer(configuration);
		return socketIOServer;
	}

	@Bean
	public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
		return new SpringAnnotationScanner(socketIOServer);
	}
	
}
