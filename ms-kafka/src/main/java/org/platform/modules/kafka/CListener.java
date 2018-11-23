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

}
