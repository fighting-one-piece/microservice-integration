package org.platform.modules.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CListener {
	
	private final Logger LOG = LoggerFactory.getLogger(CListener.class);
	
	@KafkaListener(topics = {"test"})
    public void listen(ConsumerRecord<?, ?> record) {
		LOG.info("kafka的key: " + record.key());
		LOG.info("kafka的value: " + record.value().toString());
    }
	
	@KafkaListener(topics = {"test"}, containerFactory = "kafkaListenerContainerFactory1")
    public void listen1(ConsumerRecord<?, ?> record) {
		LOG.info("c1 kafka的key: " + record.key());
		LOG.info("c1 kafka的value: " + record.value().toString());
    }
	
	@KafkaListener(topics = {"test"}, containerFactory = "kafkaListenerContainerFactory2")
    public void listen2(ConsumerRecord<?, ?> record) {
		LOG.info("c2 kafka的key: " + record.key());
		LOG.info("c2 kafka的value: " + record.value().toString());
    }

}
