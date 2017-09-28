package org.cisiondata.modules.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SpringBootApplication  
@EnableAutoConfiguration  
@ComponentScan(basePackages={"org.cisiondata"})
public class BootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);
	
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
		LOG.info("QQGraph Server Bootstrap");
	}
	
}
