package org.cisiondata.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
@AutoConfigureAfter(DataSourceConfiguration.class)
public class TransactionConfiguration implements TransactionManagementConfigurer {
	
	private static final String POINTCUT_EXPRESSION = "execution(* org.cisiondata.modules.*.service.impl.*.*(..))";  

	@Resource(name = "routingDataSouce")
	private AbstractRoutingDataSource routingDataSouce = null;
	
	@Primary
	@Bean(name = "transactionManager")
	@ConditionalOnMissingBean({PlatformTransactionManager.class})
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(routingDataSouce);
	}
	
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return transactionManager();
	}
	
	public TransactionAttributeSource transactionAttributeSource() {
		NameMatchTransactionAttributeSource transactionAttributeSource = new NameMatchTransactionAttributeSource();
		RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
		readOnlyTx.setReadOnly(true);
		readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		List<RollbackRuleAttribute> rollbackRuleAttributes = new ArrayList<RollbackRuleAttribute>();
		rollbackRuleAttributes.add(new RollbackRuleAttribute(Exception.class));
		rollbackRuleAttributes.add(new RollbackRuleAttribute(RuntimeException.class));
		RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
		requiredTx.setRollbackRules(rollbackRuleAttributes);
		requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		requiredTx.setTimeout(60);
		RuleBasedTransactionAttribute requiredNewTx = new RuleBasedTransactionAttribute();
		requiredNewTx.setRollbackRules(rollbackRuleAttributes);
		requiredNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		requiredNewTx.setTimeout(60);
		Map<String, TransactionAttribute> nameMap = new HashMap<String, TransactionAttribute>();
		nameMap.put("add*", requiredTx);
		nameMap.put("save*", requiredTx);
		nameMap.put("insert*", requiredTx);
		nameMap.put("create*", requiredTx);
		nameMap.put("update*", requiredTx);
		nameMap.put("delete*", requiredTx);
		nameMap.put("get*", readOnlyTx);
		nameMap.put("find*", readOnlyTx);
		nameMap.put("load*", readOnlyTx);
		nameMap.put("read*", readOnlyTx);
		nameMap.put("query*", readOnlyTx);
		nameMap.put("select*", readOnlyTx);
		nameMap.put("mget*", requiredNewTx);
		nameMap.put("mfind*", requiredNewTx);
		nameMap.put("mload*", requiredNewTx);
		nameMap.put("mread*", requiredNewTx);
		nameMap.put("mquery*", requiredNewTx);
		nameMap.put("mselect*", requiredNewTx);
		transactionAttributeSource.setNameMap(nameMap);
		return transactionAttributeSource;
	}
	
	@Bean(name = "transactionInterceptor")
    public TransactionInterceptor transactionInterceptor(){
        return new TransactionInterceptor(transactionManager(), transactionAttributeSource());
    }
	
	@Bean  
    public DefaultPointcutAdvisor defaultPointcutAdvisor() {  
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();  
        pointcut.setExpression(POINTCUT_EXPRESSION);  
        return new DefaultPointcutAdvisor(pointcut, transactionInterceptor());  
    } 
	
	/**
	@Bean(name = "ctransactionInterceptor")
    public CTransactionInterceptor transactionInterceptor(){
        return new CTransactionInterceptor(transactionManager());
    }

    @Bean
    public BeanNameAutoProxyCreator transactionAutoProxy() {
        BeanNameAutoProxyCreator autoProxy = new BeanNameAutoProxyCreator();
        autoProxy.setProxyTargetClass(true);//这个属性为true表示被代理的是目标类本身而不是目标类的接口
        autoProxy.setBeanNames("*Service", "*ServiceImpl");
        autoProxy.setInterceptorNames("ctransactionInterceptor");
        return autoProxy;
    }
    
    @Bean
    public AspectJExpressionPointcutAdvisor aspectJExpressionPointcutAdvisor(){
    	AspectJExpressionPointcutAdvisor pointcutAdvisor = new AspectJExpressionPointcutAdvisor();
        pointcutAdvisor.setAdvice(transactionInterceptor());
        pointcutAdvisor.setExpression(POINTCUT_EXPRESSION);
        return pointcutAdvisor;
    }
    */
    
}
