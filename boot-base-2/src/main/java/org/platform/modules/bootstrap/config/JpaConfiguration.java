package org.platform.modules.bootstrap.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EntityScan("org.platform.modules.**.entity")
@EnableJpaRepositories(
	basePackages = {"org.platform.modules.**.repo"},
	entityManagerFactoryRef = "entityManageFactory",
	transactionManagerRef = "jpaTransactionManager"
)
public class JpaConfiguration {

	@Resource(name = "routingDataSource")
	private AbstractRoutingDataSource routingDataSource = null;
	
	@Primary
    @Bean(name = "entityManageFactory")
    public LocalContainerEntityManagerFactoryBean entityManageFactory(EntityManagerFactoryBuilder builder) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = builder.dataSource(routingDataSource)
            .packages("org.platform.modules.**.entity").build();
        entityManagerFactory.setPersistenceUnitName("JpaPersistenceUnit");
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect.storage_engine", "innodb");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        properties.put("hibernate.implicit_naming_strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl");
        properties.put("hibernate.connection.charSet", "utf-8");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        entityManagerFactory.setJpaProperties(properties);
        return entityManagerFactory;
    }
	
	@Primary
    @Bean(name = "entityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder){
        return entityManageFactory(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "jpaTransactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManageFactory(builder).getObject());
    }
	
}
