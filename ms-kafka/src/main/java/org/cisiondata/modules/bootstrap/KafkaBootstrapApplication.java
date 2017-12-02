package org.cisiondata.modules.bootstrap;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class KafkaBootstrapApplication implements CommandLineRunner {
	
		private final Logger LOG = LoggerFactory.getLogger(KafkaBootstrapApplication.class);

		@Autowired
		private KafkaTemplate<String, String> template = null;

		public static void main(String[] args) {
			SpringApplication.run(KafkaBootstrapApplication.class, args);
		}


		private final CountDownLatch latch = new CountDownLatch(5);

		@Override
		public void run(String... args) throws Exception {
			this.template.send("myTopic1", "data1");
			this.template.send("myTopic1", "data2");
			this.template.send("myTopic1", "data3");
			this.template.send("myTopic1", "k1", "data4");
			this.template.send("myTopic2", "k2", "data5");
			latch.await(60, TimeUnit.SECONDS);
			LOG.info("all received");
		}

		@KafkaListener(topics = {"myTopic1", "myTopic2"})
		public void listen(ConsumerRecord<?, ?> cr) throws Exception {
			LOG.info(cr.toString());
			latch.countDown();
		}
	

}
