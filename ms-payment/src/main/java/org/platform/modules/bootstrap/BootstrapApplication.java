package org.platform.modules.bootstrap;

import org.platform.modules.bootstrap.BaseBootstrapApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

//@EnableEurekaClient
//@EnableFeignClients(basePackages = { "org.platform.modules" })
public class BootstrapApplication extends BaseBootstrapApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("Payment Server Bootstrap");
	}
}
 