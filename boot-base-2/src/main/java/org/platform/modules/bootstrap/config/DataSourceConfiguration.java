package org.platform.modules.bootstrap.config;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.bootstrap.config.ds.DataSource;
import org.platform.modules.bootstrap.config.ds.DynamicRoutingDataSource;
import org.platform.utils.spring.SpringBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class DataSourceConfiguration {

	@Value("${datasource.type}")
	private Class<? extends javax.sql.DataSource> dataSourceType = null;
	
	@Primary
	@Bean(name = "masterDataSource")
	@ConfigurationProperties(prefix = "datasource.master")
	public javax.sql.DataSource masterDataSource(){
		return DataSourceBuilder.create().type(dataSourceType).build();
	}
	
	@Bean("slaveDataSources")
	public Map<String, javax.sql.DataSource> slaveDataSources() {
		return SpringBeanFactory.getBeansOfType(javax.sql.DataSource.class);
	}
	
	@Bean(name = "routingDataSource")
	public AbstractRoutingDataSource routingDataSource() {
		DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
		Map<Object, Object> targetDataResources = new HashMap<Object, Object>();
		targetDataResources.put(DataSource.MASTER, masterDataSource());
		Map<String, javax.sql.DataSource> slaveDataSources = slaveDataSources();
		if (null != slaveDataSources) targetDataResources.putAll(slaveDataSources);
		routingDataSource.setDefaultTargetDataSource(masterDataSource());
		routingDataSource.setTargetDataSources(targetDataResources);
		return routingDataSource;
	}

}
