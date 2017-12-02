package org.cisiondata.modules.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.cisiondata.modules.bootstrap.config.KafkaProperties;

public class CKafkaStream {

	public CKafkaStream() {
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "c-stream-processing-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.kafkaServer);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		StreamsConfig config = new StreamsConfig(props);

		StreamsBuilder builder = new StreamsBuilder();
		builder.stream("topic1").mapValues(value -> value.toString() + " from topic1").to("topic2");

		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.start();
	}

	public static void main(String[] args) {
		new CKafkaStream();
	}

}
