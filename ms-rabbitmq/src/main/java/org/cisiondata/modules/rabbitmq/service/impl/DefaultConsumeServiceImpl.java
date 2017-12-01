package org.cisiondata.modules.rabbitmq.service.impl;

import org.cisiondata.modules.rabbitmq.entity.CQueue;
import org.cisiondata.modules.rabbitmq.service.impl.ConsumeServiceImpl;
import org.springframework.stereotype.Service;

@Service("defaultConsumeService")
public class DefaultConsumeServiceImpl extends ConsumeServiceImpl {

	@Override
	protected String getRoutingKey() {
		return CQueue.DEFAULT_QUEUE.getRoutingKey();
	}

	@Override
	public void handleMessage(Object message) {
		LOG.info("default consume receive message: " + message);
		System.out.println("default consume receive message: " + message);
	}

}
