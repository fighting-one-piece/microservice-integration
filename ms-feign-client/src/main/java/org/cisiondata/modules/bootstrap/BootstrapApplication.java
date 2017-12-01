package org.cisiondata.modules.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import feign.Request;
import feign.Retryer;

@Configuration
@EnableHystrix
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.cisiondata.modules.consumer.feign"})
@EnableCircuitBreaker
@SpringBootApplication  
@EnableAutoConfiguration  
@ComponentScan(basePackages={"org.cisiondata"})
public class BootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);
	
	/** 
	 * timeout设置 
	 * 默认的connectTimeout为10s readTimeout为60
	 */
	@Bean
	Request.Options feignOptions() {
		return new Request.Options(/**connectTimeoutMillis**/1 * 1000, /** readTimeoutMillis **/1 * 1000);
	}
	
	/** 
	 * retry配置
	 * 默认的retry为5次
	 */
    @Bean
    Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("Feign Client Bootstrap");
	}
	
}
