package org.platform.modules.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableHystrix
@EnableCircuitBreaker
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.platform.modules"})
public class BootstrapApplication extends BaseBootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BaseBootstrapApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("MS Base Platform Server Bootstrap");
	}
}
