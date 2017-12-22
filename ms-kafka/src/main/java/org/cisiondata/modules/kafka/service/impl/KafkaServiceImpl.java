package org.cisiondata.modules.kafka.service.impl;

import javax.annotation.Resource;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.cisiondata.modules.kafka.service.IKafkaService;
import org.cisiondata.utils.serde.SerializerUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service("kafkaService")
public class KafkaServiceImpl implements IKafkaService {

	@Resource(name = "kafkaTemplate")
	private KafkaTemplate<Object, Object> kafkaTemplate = null;
	
	@Override
	public ListenableFuture<SendResult<Object, Object>> send(Message<?> message) 
			throws RuntimeException {
		return kafkaTemplate.send(message);
	}
	
	@Override
	public ListenableFuture<SendResult<Object, Object>> send(ProducerRecord<Object, Object> record) 
			throws RuntimeException {
		return kafkaTemplate.send(record);
	}
	
	@Override
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object data) 
			throws RuntimeException {
		return kafkaTemplate.send(topic, SerializerUtils.write(data));
	}
	
	@Override
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object key, Object data) 
			throws RuntimeException {
		return kafkaTemplate.send(topic, key, SerializerUtils.write(data));
	}
	
}
