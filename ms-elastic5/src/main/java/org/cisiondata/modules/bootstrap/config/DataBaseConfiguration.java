package org.cisiondata.modules.bootstrap.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement  
@Component("databaseConfiguration")
@ConfigurationProperties(prefix = "jdbc")
@PropertySource("classpath:database/jdbc.properties")
public class DataBaseConfiguration implements EnvironmentAware {
	
	private RelaxedPropertyResolver propertyResolver = null;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "jdbc.");  
	}  
	
	@Primary
	@Bean(name = "dataSource", destroyMethod = "close")
	public DataSource dbcpDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(propertyResolver.getProperty("url"));  
		dataSource.setDriverClassName(propertyResolver.getProperty("driverClassName"));  
        dataSource.setUsername(propertyResolver.getProperty("username"));  
        dataSource.setPassword(propertyResolver.getProperty("password"));  
		return dataSource;
	}
	
}
