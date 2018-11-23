package org.platform.modules.bootstrap.config;

import javax.annotation.Resource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.CustomSqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.platform.modules.bootstrap.config.fs.SpringBootVFS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Repository;

@Configuration
@ConditionalOnClass({SqlSessionFactoryBean.class})
@AutoConfigureAfter(DataSourceConfiguration.class)
@PropertySource("classpath:mybatis/mybatis.properties")
public class MyBatisConfiguration {

	@Resource(name = "routingDataSouce")
	private AbstractRoutingDataSource routingDataSouce = null;
	
	@Value("${mybatis.configLocation}")
	private String configLocation = null;
	
	@Value("${mybatis.mapperLocations}")
	private String mapperLocations = null;

	@Value("${mybatis.typeAliasesPackage}")
	private String typeAliasesPackage = null;
	
	/** SqlSeesion配置 */
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		VFS.addImplClass(SpringBootVFS.class);
		SqlSessionFactoryBean sqlSessionFactoryBean = new CustomSqlSessionFactoryBean();
		sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
		sqlSessionFactoryBean.setDataSource(routingDataSouce);
		sqlSessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
		sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
		/**
		sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
		*/
		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
}

@Configuration
@AutoConfigureAfter(MyBatisConfiguration.class)
@ConditionalOnClass({MapperScannerConfigurer.class})
/**
@PropertySource("classpath:mybatis/mybatis.properties")
@MapperScan(basePackages = "org.platform.modules.**.dao")
*/
class MyBatisMapperConfiguration {
	
	@Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("org.platform.modules.**.dao");
        mapperScannerConfigurer.setAnnotationClass(Repository.class);
        return mapperScannerConfigurer;
    }
	
}
