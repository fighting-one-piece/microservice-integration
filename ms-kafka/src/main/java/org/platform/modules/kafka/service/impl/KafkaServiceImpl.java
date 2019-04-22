package org.platform.modules.kafka.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.platform.modules.kafka.service.IKafkaService;
import org.platform.utils.serde.SerializerUtils;
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
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object... datas) 
			throws RuntimeException {
		if (null == datas || datas.length % 2 != 0) return null;
		Map<Object, Object> dataParams = new HashMap<Object, Object>();
		for (int i = 0, len = datas.length; i < len;) {
			dataParams.put(datas[i++], datas[i++]);
		}
		return kafkaTemplate.send(topic, SerializerUtils.write(dataParams));
	}
	
	@Override
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object key, Object data) 
			throws RuntimeException {
		return kafkaTemplate.send(topic, key, SerializerUtils.write(data));
	}
	
}
