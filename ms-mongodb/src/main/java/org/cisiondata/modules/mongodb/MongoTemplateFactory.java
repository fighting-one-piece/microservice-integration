package org.cisiondata.modules.mongodb;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.utils.idgen.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

public class MongoTemplateFactory {
	
	private static Logger LOG = LoggerFactory.getLogger(MongoTemplateFactory.class);
	
	private static Map<String, MongoTemplate> instances = new HashMap<String, MongoTemplate>();
	
	public static String createMongoTemplateInstance(String host, int port, 
			String database) throws RuntimeException {
		String instanceId = genInstanceId(host, port, database);
		if (instances.containsKey(instanceId)) {
			throw new RuntimeException("instanceId has existed!"); 
		}
		instances.put(instanceId, createMongoTemplate(host, port, database));
		return instanceId;
	}
	
	public static MongoTemplate getMongoTemplate(String instanceId) throws RuntimeException {
		if (!instances.containsKey(instanceId)) {
			throw new RuntimeException("instanceId is not exists!");
		}
		return instances.get(instanceId);
	}

	private static String genInstanceId(String host, int port, String database) {
		return MD5Utils.hash(host + port + database);
	}
	
	private static MongoTemplate createMongoTemplate(String host, int port, String database) {
		try {
			MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(host, port), database);
			return new MongoTemplate(mongoDbFactory);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException("create mongodb template error!");
		}
	}

}
