package org.cisiondata.modules.bootstrap.config.ds;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {

	public static final Logger LOG = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

	@Around("execution(* org.cisiondata.modules.*.service.impl.*.*(..))")
	public Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		try {
			MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
	        Method targetMethod = methodSignature.getMethod();
	        if(targetMethod.isAnnotationPresent(TargetDataSource.class)){
	            String targetDataSource = targetMethod.getAnnotation(TargetDataSource.class).value();
	            DataSourceContextHolder.setDataSource(targetDataSource);
	        }
			return proceedingJoinPoint.proceed();
		} finally {
			DataSourceContextHolder.clearDataSource();
		}
	}

}
