package org.cisiondata.modules.rabbitmq.entity;

public enum CQueue {
	
	DEFAULT_QUEUE("default-queue", "default-routingKey"),
	DEFAULT_TOPIC_QUEUE("topic-queue", "topic.default"),
	PUSH_QUEUE("push-queue", "push-routingKey");
	
	private String name = null;
	
	private String routingKey = null;
	
	private CQueue(String name, String routingKey) {
		this.name = name;
		this.routingKey = routingKey;
	}

	public String getName() {
		return name;
	}

	public String getRoutingKey() {
		return routingKey;
	}


}
