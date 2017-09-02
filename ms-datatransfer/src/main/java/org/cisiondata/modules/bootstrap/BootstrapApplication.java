package org.cisiondata.modules.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.TemplateResolver;

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
	
	@Bean(name = "templateEngine")
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		TemplateResolver templateResolver = new TemplateResolver();
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("Data Transfer Server Bootstrap");
	}
	
}
