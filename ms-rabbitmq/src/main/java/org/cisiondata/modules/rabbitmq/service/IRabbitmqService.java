package org.cisiondata.modules.rabbitmq.service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;

public interface IRabbitmqService {
	
	/**
	 * 定义声明Exchange
	 * @param exchange
	 */
	public void declareExchange(Exchange exchange);

	/**
	 * 定义声明Queue
	 * @param queue
	 */
	public void declareQueue(Queue queue);
	
	/**
	 * 定义声明Binding
	 * @param binding
	 */
	public void declareBinding(Binding binding);
	
	/**
	 * 发送消息,默认队列
	 * @param message
	 */
	public void sendMessage(Object message);
	
	/**
	 * 发送消息,指定routingKey队列
	 * @param routingKey
	 * @param message
	 */
	public void sendMessage(String routingKey, Object message);
	
	/**
	 * 发送消息,指定exchange交换器,指定routingKey队列
	 * @param exchange
	 * @param routingKey
	 * @param message
	 */
	public void sendMessage(String exchange, String routingKey, Object message);
	
	/**
	 * 接受指定队列消息
	 * @param queueName
	 * @return
	 */
	public Object receiveMessage(String queueName);
	
	/**
	 * 接受指定队列消息,设置超时时间
	 * @param queueName
	 * @param timeout
	 * @return
	 */
	public Object receiveMessage(String queueName, long timeout);
	
}
