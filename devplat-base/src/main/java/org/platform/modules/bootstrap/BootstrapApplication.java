package org.platform.modules.bootstrap;

import org.platform.modules.bootstrap.config.RibbonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableHystrix
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.platform.modules"})
@RibbonClient(name = "microservice-client", configuration = RibbonConfiguration.class)  
public class BootstrapApplication extends BaseBootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BaseBootstrapApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("DevPlat Server Bootstrap");
	}
}
