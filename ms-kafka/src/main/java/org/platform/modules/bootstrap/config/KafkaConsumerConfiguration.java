package org.platform.modules.bootstrap.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

@Configuration
public class KafkaConsumerConfiguration {

	@Value("${kafka.consumer.servers}")
	private String servers = null;
	@Value("${kafka.consumer.enable.auto.commit}")
	private boolean enableAutoCommit = false;
	@Value("${kafka.consumer.session.timeout}")
	private Integer sessionTimeout = null;
	@Value("${kafka.consumer.auto.commit.interval}")
	private Integer autoCommitInterval = null;
	@Value("${kafka.consumer.group.id}")
	private String groupId = null;
	@Value("${kafka.consumer1.group.id:${kafka.consumer.group.id}}")
	private String groupId1 = null;
	@Value("${kafka.consumer2.group.id:${kafka.consumer.group.id}}")
	private String groupId2 = null;
	@Value("${kafka.consumer.auto.offset.reset}")
	private String autoOffsetReset = null;
	@Value("${kafka.consumer.concurrency}")
	private Integer concurrency = 10;
	@Value("${kafka.consumer.max.poll.records}")
	private String maxPollRecords = null;
	@Value("${kafka.consumer.max.poll.interval.ms}")
	private String maxPollIntervalMs = null;

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(concurrency);
		factory.getContainerProperties().setPollTimeout(4500);
		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(AckMode.RECORD);
		/**
	    factory.getContainerProperties().setErrorHandler(new SeekToCurrentErrorHandler());
		**/
		return factory;
	}
	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory1() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory1());
		factory.setConcurrency(concurrency);
		factory.getContainerProperties().setPollTimeout(4500);
		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(AckMode.RECORD);
		return factory;
	}
	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory2() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
		factory.setConsumerFactory(consumerFactory2());
		factory.setConcurrency(concurrency);
		factory.getContainerProperties().setPollTimeout(4500);
		factory.getContainerProperties().setAckOnError(false);
		factory.getContainerProperties().setAckMode(AckMode.RECORD);
		return factory;
	}
	
	public ConsumerFactory<String, String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs());
	}
	
	public ConsumerFactory<String, String> consumerFactory1() {
		Map<String, Object> consumerConfigs = consumerConfigs();
		consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId1);
		return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs);
	}
	
	public ConsumerFactory<String, String> consumerFactory2() {
		Map<String, Object> consumerConfigs = consumerConfigs();
		consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId2);
		return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs);
	}

	public Map<String, Object> consumerConfigs() {
		Map<String, Object> consumerConfigs = new HashMap<String, Object>();
		consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		consumerConfigs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
		consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
		consumerConfigs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
		consumerConfigs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
		consumerConfigs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
		consumerConfigs.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
		consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
		return consumerConfigs;
	}
	
	/**
	@Bean
	public SeekToCurrentErrorHandler seekToCurrentErrorHandler() {
		return new SeekToCurrentErrorHandler();
	}
	**/

}
