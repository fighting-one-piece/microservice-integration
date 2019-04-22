package org.platform.modules.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;

public interface IKafkaService {
	
	/**
	 * 发送消息
	 * @param message
	 * @throws BusinessException
	 */
	public ListenableFuture<SendResult<Object, Object>> send(Message<?> message) 
			throws RuntimeException;
	
	/**
	 * 发送消息
	 * @param record
	 * @return
	 * @throws RuntimeException
	 */
	public ListenableFuture<SendResult<Object, Object>> send(ProducerRecord<Object, Object> record) 
			throws RuntimeException;
	
	/**
	 * 发送消息
	 * @param topic 主题
	 * @param data 数据
	 * @throws BusinessException
	 */
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object data) 
			throws RuntimeException;
	
	/**
	 * 发送消息
	 * @param topic 主题
	 * @param datas 数据
	 * @throws BusinessException
	 */
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object... datas)
			throws RuntimeException;
	
	/**
	 * 发送消息
	 * @param topic 主题
	 * @param key
	 * @param data 数据
	 * @throws BusinessException
	 */
	public ListenableFuture<SendResult<Object, Object>> send(String topic, Object key, Object data) 
			throws RuntimeException;

}
