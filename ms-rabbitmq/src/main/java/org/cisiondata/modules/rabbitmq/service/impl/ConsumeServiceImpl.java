package org.cisiondata.modules.rabbitmq.service.impl;

import org.cisiondata.modules.rabbitmq.service.IConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConsumerServiceImpl implements IConsumerService {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected abstract String getRoutingKey();

	public abstract void handleMessage(Object message);
	
	public void handleMessage(String routingKey, Object message) {
		if (!routingKey.equalsIgnoreCase(getRoutingKey())) return;
		handleMessage(message);
	}

}
