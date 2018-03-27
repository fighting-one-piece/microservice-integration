package org.cisiondata.utils.aspect;

import java.net.ConnectException;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.bootstrap.annotation.ServiceAspectExclude;
import org.cisiondata.modules.bootstrap.config.ds.DataSource;
import org.cisiondata.modules.bootstrap.config.ds.DataSourceContextHolder;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLayerAspect {

	private Logger LOG = LoggerFactory.getLogger(ServiceLayerAspect.class);

	private static final String EXECUTION = "execution(* org.cisiondata.modules.*.service.impl.*.*(..))";
	
	private static Set<Integer> notPrintStackTraceResultCode = new HashSet<Integer>();
	
	static {
		ResultCode[] resultCodes = ResultCode.values();
		for (int i = 0, len = resultCodes.length; i < len; i++) {
			int code = resultCodes[i].getCode();
			if (code == ResultCode.SYSTEM_IS_BUSY.getCode()) {
				continue;
			}
			notPrintStackTraceResultCode.add(code);
		}
	}
	
	/**
	@Before(EXECUTION)
	public void logBefore(JoinPoint joinPoint){
		LOG.info("------Log Before Method------" + joinPoint.getSignature().getName());
	}

	@After(EXECUTION)
	public void logAfter(JoinPoint joinPoint){
		LOG.info("------Log After Method------" + joinPoint.getSignature().getName());
	}

	@AfterReturning(pointcut = EXECUTION, returning = "result")
	public void logAfterReturn(JoinPoint joinPoint, Object result) {
		LOG.info("------Log After Returning Method------" + joinPoint.getSignature().getName());
		LOG.info("------Log After Returning Method Return Value------" + result);
	}

	@AfterThrowing(pointcut = EXECUTION, throwing = "exception")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable exception){
		LOG.info("------Log After Throwing Method------" + joinPoint.getSignature().getName());
		LOG.error(exception.getMessage(), exception);
	}
	*/

	@Around(EXECUTION)
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		ServiceAspectExclude serviceAspectExclude = proceedingJoinPoint.getTarget()
			.getClass().getAnnotation(ServiceAspectExclude.class);
		if (null != serviceAspectExclude) return proceedingJoinPoint.proceed();
		long startTime = System.currentTimeMillis();
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();
		LOG.info("------Class {} Method {} Log Start", className, methodName);
		Object result = null;
		try {
			result = proceedingJoinPoint.proceed();
		} catch (BusinessException be) {
			if (notPrintStackTraceResultCode.contains(be.getCode())) {
				LOG.error(be.getMessage());
			} else {
				LOG.error(be.getMessage(), be);
			}
			throw be;
		} catch (ConnectException ce) {
			DataSourceContextHolder.setDataSource(DataSource.MASTER);
			return proceedingJoinPoint.proceed();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(ResultCode.SYSTEM_IS_BUSY);
		} finally {
			LOG.info("------Class {} Method {} Log End ! Spend Time: {} s", className, methodName, 
					(System.currentTimeMillis() - startTime) / 1000);
		}
		return result;
	}
	
}
