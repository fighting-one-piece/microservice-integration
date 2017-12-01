package org.cisiondata.modules.rabbitmq.service;

public interface IConsumeService {
	
	/**
	 * 处理指定队列消息
	 * @param message
	 */
	public void handleMessage(String exchange, String routingKey, Object message);
	
}
