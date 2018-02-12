package org.cisiondata.modules.bootstrap;

import org.cisiondata.modules.bootstrap.config.RibbonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableHystrix
@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"org.cisiondata.modules"})
@RibbonClient(name = "microservice-client", configuration = RibbonConfiguration.class)  
public class BootstrapApplication extends BaseBootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BaseBootstrapApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("DevPlat Server Bootstrap");
	}
}
