package org.platform.modules.bootstrap;

import org.platform.modules.bootstrap.BaseBootstrapApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
public class BootstrapApplication extends BaseBootstrapApplication {

	private static Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("Instant Messaging Server Bootstrap");
	}

}
