package org.platform.modules.rabbitmq.service.impl;

import org.platform.modules.rabbitmq.service.IConsumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConsumeServiceImpl implements IConsumeService {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected abstract String getRoutingKey();

	public abstract void handleMessage(Object message);
	
	@Override
	public void handleMessage(String exchange, String routingKey, Object message) {
		if ("directExchange".equalsIgnoreCase(exchange)) {
			handleDirectMessage(routingKey, message);
		} else if ("topicExchange".equalsIgnoreCase(exchange)) {
			handleTopicMessage(routingKey, message);
		}
	}
	
	private void handleDirectMessage(String routingKey, Object message) {
		if (!routingKey.equalsIgnoreCase(getRoutingKey())) return;
		handleMessage(message);
	}
	
	private void handleTopicMessage(String routingKey, Object message) {
		if (!routingKey.matches(getRoutingKey())) return;
		handleMessage(message);
	}

}
