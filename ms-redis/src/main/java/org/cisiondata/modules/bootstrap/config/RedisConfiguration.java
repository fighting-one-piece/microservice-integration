package org.cisiondata.modules.bootstrap.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.cisiondata.utils.redis.JedisClusterFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableAutoConfiguration
public class RedisConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.redis.pool")
	public JedisPoolConfig getJedisPoolConfig() {
		return new JedisPoolConfig();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.redis.pool")
	public GenericObjectPoolConfig getGenericObjectPoolConfig() {
		return new GenericObjectPoolConfig();
	}
	
	@Bean(name = "jedisCluster")
	@ConfigurationProperties(prefix = "spring.redis.cluster")
	public JedisClusterFactory getJedisClusterFactory() {
		JedisClusterFactory jedisClusterFactory = new JedisClusterFactory();
		jedisClusterFactory.setGenericObjectPoolConfig(getGenericObjectPoolConfig());
		return jedisClusterFactory;
	}
	
}
