package org.platform.modules.bootstrap.config;

import org.platform.modules.websocket.handler.SystemWebSocketHandler;
import org.platform.modules.websocket.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@EnableWebMvc
@Configuration
@EnableWebSocket
public class WebSocketConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(systemWebSocketHandler(), "/webSocketServer").addInterceptors(webSocketHandshakeInterceptor());  
		registry.addHandler(systemWebSocketHandler(), "/webSocketServer/sockjs")
			.setAllowedOrigins("*").addInterceptors(webSocketHandshakeInterceptor()).withSockJS();  
	}
	
	@Bean  
	public WebSocketHandler systemWebSocketHandler(){  
		return new SystemWebSocketHandler();  
	} 
	
	@Bean
	public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor(){
		return new WebSocketHandshakeInterceptor();
	}
	
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
	
}
