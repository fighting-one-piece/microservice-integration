package org.platform.modules.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.platform.modules.bootstrap.config.KafkaProperties;

public class CKafkaProducer extends Thread {
	
	private final Producer<String, String> producer;
	private final String topic;
	private final Properties props = new Properties();

	public static void main(String[] args) {
		CKafkaProducer producerThread = new CKafkaProducer(KafkaProperties.topic1);
		producerThread.start();
	}

	public CKafkaProducer(String topic) {
		props.put("bootstrap.servers", KafkaProperties.kafkaServer);
		props.put("acks", "all");
		props.put("retries", 0);
		// props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		producer = new KafkaProducer<>(props);
		this.topic = topic;
	}

	@Override
	public void run() {
		int messageNo = 1;
		while (true) {
			String messageStr = new String("Message_" + messageNo);
			System.out.println("Send:" + messageStr);
			producer.send(new ProducerRecord<>(topic, "key_" + messageNo, messageStr), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					} else {
						System.out.println("The offset of the record we just sent is: " + metadata.offset());
					}
				}
			});
			messageNo++;
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
