package org.cisiondata.modules.bootstrap.config;

public class KafkaProperties {
	
	public final static String zkConnect = "127.0.0.1:2181";
	public final static String groupId = "group1";
	public final static String kafkaServer = "localhost:9092";
	public final static int kafkaProducerBufferSize = 64 * 1024;
	public final static int connectionTimeOut = 20000;
	public final static int reconnectInterval = 10000;
	public final static String topic1 = "topic1";
	public final static String topic2 = "topic2";
	public final static String topic3 = "topic3";
	public final static String clientId = "SimpleConsumerClient";

}
