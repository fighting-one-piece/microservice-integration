package org.platform.modules.bootstrap.config;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

/** 根据配置文件创建MongoDbFactory */
public abstract class AbstractMongoConfiguration {
	
	private String host = null;
	
	private String database = null;
	
	private String username = null;
	
	private String password = null;
	
	private int port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * Method that creates MongoDbFactory Common to both of the MongoDb
	 * connections
	 */
	public MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new MongoClient(host, port), database);
	}

	/*
	 * Factory method to create the MongoTemplate
	 */
	abstract public MongoTemplate getMongoTemplate() throws Exception;
	
}