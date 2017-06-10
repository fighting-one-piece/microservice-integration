package org.cisiondata.modules.rabbitmq.service;

public interface IConsumerService {

	/**
	 * 处理消息
	 * @param routingKey
	 * @param message
	 */
	public void handleMessage(String routingKey, Object message);
	
}
