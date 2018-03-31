package org.cisiondata.modules.bootstrap.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.cisiondata.utils.redis.JedisClusterFactory;
import org.cisiondata.utils.redis.RedisObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnClass({Jedis.class, JedisConnection.class, JedisConnectionFactory.class, RedisTemplate.class})
public class RedisConfiguration {

	private Logger LOG = LoggerFactory.getLogger(RedisConfiguration.class);
	
	@Bean
	@ConfigurationProperties(prefix = "spring.redis.pool")
	public JedisPoolConfig jedisPoolConfig() {
		return new JedisPoolConfig();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.redis.pool")
	public GenericObjectPoolConfig genericObjectPoolConfig() {
		return new GenericObjectPoolConfig();
	}
	
	@Bean(name = "jedisConnectionFactory")
	@ConfigurationProperties(prefix = "spring.redis.pool")
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setPoolConfig(jedisPoolConfig());
		LOG.info("Jedis Connection Factory Initialize Success!");
		return jedisConnectionFactory;
	}
	
	/**
	@Bean
	public RedisTemplate<?, ?> getRedisTemplate() {
		return new StringRedisTemplate(jedisConnectionFactory());
	}
	**/
	
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
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
