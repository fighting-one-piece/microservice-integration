package org.cisiondata.modules.bootstrap.interceptor;

import java.io.Serializable;
import java.util.Properties;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {

	private static final long serialVersionUID = 1L;
	
	public TransactionInterceptor() {
		setTransactionAttributes(transactionAttributes());
    }
    
    private Properties transactionAttributes(){
        Properties attributes = new Properties();
        attributes.setProperty("save*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT");
        attributes.setProperty("create*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT");
        attributes.setProperty("insert*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT");
        attributes.setProperty("update*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT");
        attributes.setProperty("delete*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT");
        attributes.setProperty("get*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly");
        attributes.setProperty("find*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly");
        attributes.setProperty("read*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly");
        attributes.setProperty("query*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly");
        attributes.setProperty("select*", "PROPAGATION_REQUIRED,ISOLATION_DEFAULT,readOnly");
        return attributes;
    }

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        // Adapt to TransactionAspectSupport's invokeWithinTransaction...
        return invokeWithinTransaction(invocation.getMethod(), targetClass, () -> invocation.proceed());
	}

}
