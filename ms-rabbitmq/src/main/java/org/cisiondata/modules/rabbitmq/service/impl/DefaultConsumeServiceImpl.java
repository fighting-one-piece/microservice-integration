package org.cisiondata.modules.rabbitmq.service.impl;

import org.cisiondata.modules.bootstrap.config.RabbitmqConfiguration;
import org.cisiondata.modules.rabbitmq.service.impl.ConsumerServiceImpl;
import org.springframework.stereotype.Service;

@Service("defaultConsumerService")
public class DefaultConsumerServiceImpl extends ConsumerServiceImpl {

	@Override
	protected String getRoutingKey() {
		return RabbitmqConfiguration.DEFAULT_ROUTINGKEY;
	}

	@Override
	public void handleMessage(Object message) {
		LOG.info("default consumer receive message: " + message);
		System.out.println("default consumer receive message: " + message);
	}

}
