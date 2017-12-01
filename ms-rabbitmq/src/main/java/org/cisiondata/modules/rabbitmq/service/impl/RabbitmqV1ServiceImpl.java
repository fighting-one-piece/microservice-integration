package org.cisiondata.modules.rabbitmq.service.impl;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.config.RabbitmqConfiguration;
import org.cisiondata.modules.rabbitmq.service.IRabbitmqV1Service;
import org.cisiondata.utils.serde.SerializerUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rabbitmqService")
public class RabbitmqV1ServiceImpl implements IRabbitmqV1Service {
	
	@Autowired
	private AmqpAdmin amqpAdmin = null;

	@Resource(name = "amqpTemplate")
    private AmqpTemplate amqpTemplate = null;
	
	@Autowired
	private SimpleMessageListenerContainer container = null;
	
    @Override
	public void declareExchange(Exchange exchange) {
		amqpAdmin.declareExchange(exchange);
	}
    
    @Override
    public void declareQueue(Queue queue) {
    	amqpAdmin.declareQueue(queue);
    	container.addQueues(queue);
    }
    
    @Override
    public void declareBinding(Binding binding) {
    	amqpAdmin.declareBinding(binding);
    }
    
	@Override
	public void sendMessage(Object message) {
		amqpTemplate.convertAndSend(RabbitmqConfiguration.DEFAULT_ROUTINGKEY, SerializerUtils.write(message));
	}
	
	@Override
	public void sendMessage(String routingKey, Object message) {
		amqpTemplate.convertAndSend(routingKey, SerializerUtils.write(message));
	}
	
	@Override
	public void sendMessage(String exchange, String routingKey, Object message) {
		amqpTemplate.convertAndSend(exchange, routingKey, SerializerUtils.write(message));
	}

	@Override
	public Object receiveMessage(String queueName) {
		return amqpTemplate.receiveAndConvert(queueName);
	}

	@Override
	public Object receiveMessage(String queueName, long timeout) {
		return amqpTemplate.receiveAndConvert(queueName, timeout);
	}

	
}
