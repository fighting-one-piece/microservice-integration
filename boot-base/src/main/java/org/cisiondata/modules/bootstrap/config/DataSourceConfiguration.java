package org.cisiondata.modules.bootstrap.config;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.modules.bootstrap.config.ds.DataSource;
import org.cisiondata.modules.bootstrap.config.ds.DynamicRoutingDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
	
	@Bean(name = "slaveDataSource")
	@ConfigurationProperties(prefix = "datasource.slave")
	public javax.sql.DataSource slaveDataSource(){
		return DataSourceBuilder.create().type(dataSourceType).build();
	}
	
	@Bean(name = "routingDataSouce")
	public AbstractRoutingDataSource routingDataSouce() {
		DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
		Map<Object, Object> targetDataResources = new HashMap<Object, Object>();
		targetDataResources.put(DataSource.MASTER, masterDataSource());
		targetDataResources.put(DataSource.SLAVE, slaveDataSource());
		routingDataSource.setDefaultTargetDataSource(masterDataSource());
		routingDataSource.setTargetDataSources(targetDataResources);
		return routingDataSource;
	}

}
