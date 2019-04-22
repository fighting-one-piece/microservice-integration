package org.platform.modules.bootstrap.config;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.bootstrap.config.ds.DataSource;
import org.platform.modules.bootstrap.config.ds.DynamicRoutingDataSource;
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
	
	/**
	@Bean(name = "slave1DataSource")
	@ConfigurationProperties(prefix = "datasource.slave1")
	public javax.sql.DataSource slave1DataSource(){
		return DataSourceBuilder.create().type(dataSourceType).build();
	}
	
	@Bean(name = "slave2DataSource")
	@ConfigurationProperties(prefix = "datasource.slave2")
	public javax.sql.DataSource slave2DataSource(){
		return DataSourceBuilder.create().type(dataSourceType).build();
	}
	*/
	
	@Bean(name = "routingDataSource")
	public AbstractRoutingDataSource routingDataSource() {
		DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
		Map<Object, Object> targetDataResources = new HashMap<Object, Object>();
		targetDataResources.put(DataSource.MASTER, masterDataSource());
		/**
		targetDataResources.put(DataSource.SLAVE1, slave1DataSource());
		targetDataResources.put(DataSource.SLAVE2, slave2DataSource());
		*/
		routingDataSource.setDefaultTargetDataSource(masterDataSource());
		routingDataSource.setTargetDataSources(targetDataResources);
		return routingDataSource;
	}

}
