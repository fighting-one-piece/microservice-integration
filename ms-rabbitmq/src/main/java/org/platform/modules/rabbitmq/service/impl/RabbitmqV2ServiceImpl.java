package org.platform.modules.rabbitmq.service.impl;

import javax.annotation.Resource;

import org.platform.modules.rabbitmq.entity.CQueue;
import org.platform.modules.rabbitmq.service.IRabbitmqV2Service;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rabbitmqV2Service")
public class RabbitmqV2ServiceImpl implements IRabbitmqV2Service {
	
	@Resource(name="amqpAdmin")
	private AmqpAdmin amqpAdmin = null;

	@Resource(name="dAmqpTemplate")
    private AmqpTemplate dAmqpTemplate = null;
	
	@Resource(name="tAmqpTemplate")
    private AmqpTemplate tAmqpTemplate = null;
	
//	@Resource(name="messageListenerContainer")
	@Autowired
	private SimpleMessageListenerContainer listenerContainer = null;
	
    @Override
	public void declareExchange(Exchange exchange) {
		amqpAdmin.declareExchange(exchange);
	}
    
    @Override
    public void declareQueue(Queue queue) {
    	amqpAdmin.declareQueue(queue);
    	listenerContainer.addQueues(queue);
    }
    
    @Override
    public void declareBinding(Binding binding) {
    	amqpAdmin.declareBinding(binding);
    }
    
	@Override
	public void sendMessage(Object message) {
		dAmqpTemplate.convertAndSend(CQueue.DEFAULT_QUEUE.getRoutingKey(), message);
	}
	
	@Override
	public void sendTopicMessage(Object message) {
		tAmqpTemplate.convertAndSend(CQueue.DEFAULT_TOPIC_QUEUE.getRoutingKey(), message);
	}

	@Override
	public void sendMessage(String routingKey, Object message) {
		System.out.println("send routingKey: " + routingKey + " message: " + message);
		dAmqpTemplate.convertAndSend(routingKey, message);
	}
	
	@Override
	public void sendTopicMessage(String routingKey, Object message) {
		System.out.println("send routingKey: " + routingKey + " message: " + message);
		tAmqpTemplate.convertAndSend(routingKey, message);
	}

	@Override
	public void sendMessage(String exchange, String routingKey, Object message) {
		System.out.println("send exchange: " + exchange + " routingKey: " + routingKey + " message: " + message);
		dAmqpTemplate.convertAndSend(exchange, routingKey, message);
	}
	
	@Override
	public void sendTopicMessage(String exchange, String routingKey, Object message) {
		tAmqpTemplate.convertAndSend(exchange, routingKey, message);
	}

	@Override
	public Object receiveMessage(String queueName) {
		return dAmqpTemplate.receiveAndConvert(queueName);
	}

	@Override
	public Object receiveMessage(String queueName, long timeout) {
		return dAmqpTemplate.receiveAndConvert(queueName, timeout);
	}

	
}
