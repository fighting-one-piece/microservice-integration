package org.cisiondata.modules.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "org.cisiondata" })
public class BootstrapDataPlat extends BootstrapApplication {

	public static Logger LOG = LoggerFactory.getLogger(BootstrapDataPlat.class);

	public static void main(String[] args) {
		SpringApplication.run(BootstrapDataPlat.class, args);
		LOG.info("DataPlat Server Bootstrap");
	}

}
