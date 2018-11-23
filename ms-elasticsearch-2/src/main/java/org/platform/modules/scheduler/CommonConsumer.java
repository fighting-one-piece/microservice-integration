package org.platform.modules.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.platform.modules.scheduler.service.IConsumeService;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class CommonConsumer extends Thread {

	private String topic = null;
	
	private IConsumeService consumerService = null;
	
	public CommonConsumer(String topic) {
		super();
		this.topic = topic;
	}
	
	public CommonConsumer(String topic, IConsumeService consumeService) {
		super();
		this.topic = topic;
		this.consumerService = consumeService;
	}

	@Override
	public void run() {
		ConsumerConnector consumer = createConsumerConnector();
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = messageStreams.get(topic);
		for (int i = 0, len = streams.size(); i < len; i++) {
			ConsumerIterator<byte[], byte[]> iterator = streams.get(i).iterator();
			while (iterator.hasNext()) {
				String message = new String(iterator.next().message());
				consumerService.handle(message);
			}
		}
		System.out.println("consumer finish!");
	}

	private ConsumerConnector createConsumerConnector() {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "192.168.0.15:2181,192.168.0.16:2181,192.168.0.17:2181/kafka");
		properties.put("zookeeper.session.timeout.ms", "4000");
		properties.put("zookeeper.sync.time.ms", "200");
		properties.put("enable.auto.commit", "true");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("session.timeout.ms", "30000");
		//必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
		properties.put("group.id", "platform-consumer-group");
		return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
	}

}
