package org.platform.modules.bootstrap.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfiguration {
	
	@Value("${kafka.producer.servers}")
    private String servers = null;
    @Value("${kafka.producer.retries}")
    private int retries;
    @Value("${kafka.producer.batch.size}")
    private int batchSize;
    @Value("${kafka.producer.linger}")
    private int linger;
    @Value("${kafka.producer.buffer.memory}")
    private int bufferMemory;


    public Map<String, Object> producerConfigs() {
        Map<String, Object> producerConfigs = new HashMap<String, Object>();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        producerConfigs.put(ProducerConfig.RETRIES_CONFIG, retries);
        producerConfigs.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        producerConfigs.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        producerConfigs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        return producerConfigs;
    }

    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<String, String>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }

}
