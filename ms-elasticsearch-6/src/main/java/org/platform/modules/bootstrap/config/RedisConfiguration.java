package org.platform.modules.bootstrap.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.platform.utils.redis.JedisClusterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.Jedis;

@Configuration
@ConditionalOnClass({Jedis.class})
public class RedisConfiguration {

	private Logger LOG = LoggerFactory.getLogger(RedisConfiguration.class);
	
	@SuppressWarnings("rawtypes")
	@Bean
	@ConfigurationProperties(prefix = "spring.redis.pool")
	public GenericObjectPoolConfig genericObjectPoolConfig() {
		return new GenericObjectPoolConfig();
	}
	
	@Bean(name = "jedisCluster")
	@ConfigurationProperties(prefix = "spring.redis.cluster")
	public JedisClusterFactory jedisClusterFactory() {
		JedisClusterFactory jedisClusterFactory = new JedisClusterFactory();
		jedisClusterFactory.setGenericObjectPoolConfig(genericObjectPoolConfig());
		LOG.info("Jedis Cluster Factory Initialize Success!");
		return jedisClusterFactory;
	}
	
}
