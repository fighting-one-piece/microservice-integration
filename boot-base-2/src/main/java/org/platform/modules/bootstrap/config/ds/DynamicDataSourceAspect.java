package org.platform.modules.bootstrap.config.ds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.platform.utils.spring.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {

	public static final Logger LOG = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

private static final String EXECUTION = "execution(* org.cisiondata.modules.*.service.impl.*.*(..))";
	
	@Before(EXECUTION)
	public void before(JoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        if(targetMethod.isAnnotationPresent(TargetDataSource.class)){
            String targetDataSource = targetMethod.getAnnotation(TargetDataSource.class).value();
            DataSourceContextHolder.setDataSource(targetDataSource);
			try {
				Object dataSourceObj = SpringBeanFactory.getBean(targetDataSource);
				if (null != dataSourceObj) {
					javax.sql.DataSource dataSource = (javax.sql.DataSource) dataSourceObj;
					SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) SpringBeanFactory.getBean(SqlSessionFactory.class);
					Environment environment = sqlSessionFactory.getConfiguration().getEnvironment();
					Field dataSourceField = environment.getClass().getDeclaredField("dataSource");
					dataSourceField.setAccessible(true);
					dataSourceField.set(environment, dataSource); //修改MyBatis的数据源
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
        }
	}
	
	@After(EXECUTION)
	public void after(JoinPoint joinPoint) {
		DataSourceContextHolder.clearDataSource();
	}

}
