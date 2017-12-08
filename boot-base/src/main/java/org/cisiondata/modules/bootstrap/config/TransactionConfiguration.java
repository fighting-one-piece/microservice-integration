package org.cisiondata.modules.bootstrap.config;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.interceptor.TransactionInterceptor;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement
public class TransactionConfiguration implements TransactionManagementConfigurer {

	@Resource(name = "masterDataSource")
	private javax.sql.DataSource masterDataSource = null;
	
	@Bean
	@Primary
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(masterDataSource);
	}
	
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}
	
	@Bean(name = "ctransactionInterceptor")
    public TransactionInterceptor transactionInterceptor(){
        return new TransactionInterceptor();
    }

    @Bean
    public BeanNameAutoProxyCreator transactionAutoProxy() {
        BeanNameAutoProxyCreator autoProxy = new BeanNameAutoProxyCreator();
        autoProxy.setProxyTargetClass(true);//这个属性为true表示被代理的是目标类本身而不是目标类的接口
        autoProxy.setBeanNames("*ServiceImpl");
        autoProxy.setInterceptorNames("ctransactionInterceptor");
        return autoProxy;
    }

}
