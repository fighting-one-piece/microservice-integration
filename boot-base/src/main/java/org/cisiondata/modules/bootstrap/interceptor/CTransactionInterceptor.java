package org.cisiondata.modules.bootstrap.interceptor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionAttribute;

public class CTransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {

	private static final long serialVersionUID = 1L;

	public CTransactionInterceptor(PlatformTransactionManager transactionManager) {
		setTransactionManager(transactionManager);
		setTransactionAttributes(transactionAttributes());
		/**
		setTransactionAttributeSource(nameMatchTransactionAttributeSource());
		*/
	}

	public Properties transactionAttributes() {
		Properties attributes = new Properties();
		attributes.setProperty("save*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-Exception");
		attributes.setProperty("create*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-Exception");
		attributes.setProperty("insert*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-Exception");
		attributes.setProperty("update*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-Exception");
		attributes.setProperty("delete*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-Exception");
		attributes.setProperty("get*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly,-Exception");
		attributes.setProperty("find*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly,-Exception");
		attributes.setProperty("read*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly,-Exception");
		attributes.setProperty("query*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly,-Exception");
		attributes.setProperty("select*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly,-Exception");
		return attributes;
	}

	public NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource() {
		NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource = new NameMatchTransactionAttributeSource();
		/* 只读事务，不做更新操作 */
		RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
		readOnlyTx.setReadOnly(true);
		readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		/* 当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务 */
		RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
		requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		requiredTx.setTimeout(5);
		Map<String, TransactionAttribute> nameMap = new HashMap<String, TransactionAttribute>();
		nameMap.put("add*", requiredTx);
		nameMap.put("save*", requiredTx);
		nameMap.put("insert*", requiredTx);
		nameMap.put("create*", requiredTx);
		nameMap.put("update*", requiredTx);
		nameMap.put("delete*", requiredTx);
		nameMap.put("get*", readOnlyTx);
		nameMap.put("find*", readOnlyTx);
		nameMap.put("read*", readOnlyTx);
		nameMap.put("query*", readOnlyTx);
		nameMatchTransactionAttributeSource.setNameMap(nameMap);
		return nameMatchTransactionAttributeSource;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		return invokeWithinTransaction(invocation.getMethod(), targetClass, () -> invocation.proceed());
	}

}
