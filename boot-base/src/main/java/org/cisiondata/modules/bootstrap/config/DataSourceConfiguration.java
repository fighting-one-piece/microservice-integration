package org.cisiondata.modules.bootstrap.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Configuration
@Component("dataSourceConfiguration")
//@PropertySource("classpath:database/jdbc.properties")
public class DataSourceConfiguration {

	
	/**
	private RelaxedPropertyResolver propertyResolver = null;

	@Override
	public void setEnvironment(Environment environment) {
		this.propertyResolver = new RelaxedPropertyResolver(environment, "datasource.");
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
	**/

	@Value("${datasource.type}")
	private Class<? extends DataSource> dataSourceType = null;
	
	@Bean(name = "masterDataSource")
	@Primary
	@ConfigurationProperties(prefix = "datasource.master")
	public DataSource masterDataSource(){
		return DataSourceBuilder.create().type(dataSourceType).build();
	}
	
	@Bean(name = "slaveDataSource")
	@ConfigurationProperties(prefix = "datasource.slave")
	public DataSource slaveDataSource(){
		return DataSourceBuilder.create().type(dataSourceType).build();
	}

}
