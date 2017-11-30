package org.cisiondata.modules.bootstrap;

import org.cisiondata.modules.filter.CustomFilterProcessor;
import org.cisiondata.modules.filter.ErrorExtFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableZuulProxy
@EnableEurekaClient
@SpringCloudApplication  
@EnableAutoConfiguration  
@ComponentScan(basePackages={"org.cisiondata"})
public class BootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);
	
	@Bean
	public CustomFilterProcessor CustomFilterProcessor() {
		return new CustomFilterProcessor();
	}
	
	@Bean
	public ErrorExtFilter errorExtFilter() {
		return new ErrorExtFilter();
	}

	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("API Gateway Server Bootstrap");
	}
	
	
}
