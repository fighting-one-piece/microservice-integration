package org.platform.modules.bootstrap.config;

import org.platform.utils.redis.RedisObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableAutoConfiguration
public class RedisConfiguration {

	private Logger LOG = LoggerFactory.getLogger(RedisConfiguration.class);
	
	@Bean
	@ConfigurationProperties(prefix = "spring.redis")
	public JedisPoolConfig getJedisPoolConfig() {
		return new JedisPoolConfig();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "spring.redis")
	public JedisConnectionFactory getJedisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setPoolConfig(getJedisPoolConfig());
		LOG.info("Jedis Connection Factory Initialize Success!");
		return factory;
	}
	
	/**
	@Bean
	public RedisTemplate<?, ?> getRedisTemplate() {
		return new StringRedisTemplate(getJedisConnectionFactory());
	}
	**/
	
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> getRedisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(getJedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisObjectSerializer());
        return template;
	}
	
}
