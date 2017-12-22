package org.cisiondata.modules.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.primary")
public class PrimaryMongoConfiguration extends AbstractMongoConfiguration {

	@Primary
	@Override
	@Bean(name = "primaryMongoTemplate")
	public MongoTemplate getMongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

}