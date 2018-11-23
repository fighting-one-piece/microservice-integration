package org.platform.modules.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.platform.modules.scheduler.service.IConsumeService;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class ConsumerScheduler implements Runnable {
	
    private String topic = null;
    private int threadNum = 6;
    private int batchNum = 1000;
    private ExecutorService executor = null;
    private ConsumerConnector consumer = null;
    private IConsumeService consumeService = null;
    private List<ConsumerTask> tasks = new ArrayList<ConsumerTask>();
 
    public ConsumerScheduler(String topic, int threadNum, IConsumeService consumeService) {
    	this.topic = topic;
    	this.threadNum = threadNum;
        this.consumeService = consumeService;
    }
    
    public ConsumerScheduler(String topic, int threadNum, int batchNum, IConsumeService consumeService) {
    	this.topic = topic;
    	this.threadNum = threadNum;
    	this.batchNum = batchNum;
        this.consumeService = consumeService;
    }
    
    public ConsumerConfig createConsumerConfig() {
    	Properties properties = new Properties();
		properties.put("zookeeper.connect", "192.168.0.124:2181/kafka");
		properties.put("zookeeper.session.timeout.ms", "4000");
		properties.put("zookeeper.sync.time.ms", "200");
		properties.put("enable.auto.commit", "true");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("session.timeout.ms", "30000");
		// 必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
		properties.put("group.id", "platform-consumer-group");
        return new ConsumerConfig(properties);
    }
    
    public ConsumerConnector createConsumerConnector() {
		return Consumer.createJavaConsumerConnector(createConsumerConfig());
	}
    
    @Override
    public void run() {
    	consumer = createConsumerConnector();
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(threadNum));
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = messageStreams.get(topic);
 
        executor = Executors.newFixedThreadPool(threadNum);
 
        for (int i = 0, len = streams.size(); i < len; i++) {
        	ConsumerTask task = new ConsumerTask(streams.get(i), batchNum, consumeService);
        	executor.submit(task);
        	tasks.add(task);
        }
    }
    
    public void startup() {
    	this.run();
    }
    
	public void shutdown() {
		if (null != consumer) consumer.shutdown();
		for (int i = 0, len = tasks.size(); i < len; i++) {
			tasks.get(i).shutdown();
		}
		if (null != executor) executor.shutdown();
		try {
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted during shutdown, exiting uncleanly");
		}
	}
 
}

class ConsumerTask implements Runnable {
	
	private KafkaStream<byte[], byte[]> kafkaStream = null;
	private int batchNum = 1000;
	private IConsumeService consumeService = null;
	private List<String> messages = new ArrayList<String>();
 
    public ConsumerTask(KafkaStream<byte[], byte[]> kafkaStream, int batchNum, IConsumeService consumeService) {
        this.kafkaStream = kafkaStream;
        this.batchNum = batchNum;
        this.consumeService = consumeService;
    }
    
    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
		while (iterator.hasNext()) {
			MessageAndMetadata<byte[], byte[]> mam = iterator.next();
			String message = new String(mam.message());
			/**
			System.out.println(Thread.currentThread().getName() + ": partition[" + mam.partition() + "]," 
					+ "offset[" + mam.offset() + "], " + message); 
			*/
			messages.add(message);
			if (messages.size() == batchNum) {
				consumeService.handle(messages);
				messages.clear();
			}
		}
    }
    
    public int getMessageNum() {
    	return messages.size();
    }
    
    public void shutdown() {
    	if (messages.size() > 0) consumeService.handle(messages);
    }
    
}