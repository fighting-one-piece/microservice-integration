package org.platform.modules.bootstrap;

import org.platform.modules.bootstrap.BaseBootstrapApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@EnableEurekaClient
public class BootstrapApplication extends BaseBootstrapApplication {
	
	private static final Logger LOG = LoggerFactory.getLogger(BootstrapApplication.class);

	public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
	
	public static void main(String[] args) {
		SpringApplication.run(BootstrapApplication.class, args);
		LOG.info("OAuth Server Bootstrap");
	}
}
