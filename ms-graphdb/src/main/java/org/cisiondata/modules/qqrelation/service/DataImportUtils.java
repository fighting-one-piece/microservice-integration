package org.cisiondata.modules.qqrelation.service;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;
import org.cisiondata.utils.json.GsonUtils;

public class DataImportUtils {
	
	public static void produce(List<String> messages) {
		Properties properties = new Properties();  
//        properties.put("bootstrap.servers", "192.168.0.115:9092");  
        properties.put("bootstrap.servers", "192.168.0.15:9092,192.168.0.16:9092,192.168.0.17:9092");  
        properties.put("producer.type", "sync");  
        properties.put("request.required.acks", "1");  
        properties.put("serializer.class", "kafka.serializer.DefaultEncoder");  
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");  
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); 
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        for (int i = 0, len = messages.size(); i < len; i++) {
        	String message = messages.get(i);
        	producer.send(new ProducerRecord<String, String>("elastic5", i % 6, "" + i, message), new Callback() {
        		
        		@Override
        		public void onCompletion(RecordMetadata metadata, Exception exception) {
        			System.out.println(metadata.offset() + ":" + metadata.topic() + ":" 
        					+ metadata.partition());
        		}
        	});
        }
		producer.close(); 
	}
	
	public static void produce(List<String> messages, String topic, int partitions) {
		Properties properties = new Properties();  
//        properties.put("bootstrap.servers", "192.168.0.115:9092");  
        properties.put("bootstrap.servers", "192.168.0.15:9092,192.168.0.16:9092,192.168.0.17:9092");  
        properties.put("producer.type", "sync");  
        properties.put("request.required.acks", "1");  
        properties.put("serializer.class", "kafka.serializer.DefaultEncoder");  
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");  
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); 
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        for (int i = 0, len = messages.size(); i < len; i++) {
        	String message = messages.get(i);
        	producer.send(new ProducerRecord<String, String>(topic, i % partitions, "" + i, message), new Callback() {
        		
        		@Override
        		public void onCompletion(RecordMetadata metadata, Exception exception) {
        			System.out.println(metadata.offset() + ":" + metadata.topic() + ":" 
        					+ metadata.partition());
        		}
        	});
        }
		producer.close(); 
	}
	
	public static void insertKafkaDatas() {
		try {
//			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201704\\logistics", new DefaultLineHandler());
			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201704\\gongjijin\\gjj_01.txt", new DefaultLineHandler());
			produce(lines);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void insertKafkaElasticDatas() {
		try {
			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201705\\internet.txt", new DefaultLineHandler());
			produce(lines, "elastic5", 12);
			System.out.println(lines.size() + " records produce finish!!!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void insertKafkaQQNodeDatas() {
		try {
			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201705\\qq20000.txt", new DefaultLineHandler());
			produce(lines, "qqnode", 6);
			System.out.println(lines.size() + " records produce finish!!!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void testKafkaElasticDatas() {
		try {
			List<String> lines = FileUtils.readFromAbsolute("F:\\document\\doc\\201705\\internet.txt", new DefaultLineHandler());
			Set<String> ids = new HashSet<String>();
			for (String line : lines) {
				Map<String, Object> map = GsonUtils.fromJsonToMap(line);
				ids.add(String.valueOf(map.get("_id")).trim());
			}
			System.out.println(ids.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		insertKafkaQQNodeDatas();
//		testKafkaElasticDatas();
	}
	
}
