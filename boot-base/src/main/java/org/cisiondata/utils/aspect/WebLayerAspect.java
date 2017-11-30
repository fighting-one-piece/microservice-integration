package org.cisiondata.utils.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Aspect
@Component
public class WebLayerAspect {

	private Logger LOG = LoggerFactory.getLogger(WebLayerAspect.class);

	private static final String EXECUTION = "execution(* org.cisiondata.modules.*.controller.*.*(..))";
	
	@Before(EXECUTION)
	public void before(JoinPoint joinPoint){
	}

	@After(EXECUTION)
	public void after(JoinPoint joinPoint){
	}

	@AfterReturning(pointcut = EXECUTION, returning = "result")
	public void afterReturn(JoinPoint joinPoint, Object result) {
	}

	@AfterThrowing(pointcut = EXECUTION, throwing = "exception")
	public void afterThrowing(JoinPoint joinPoint, Throwable exception){
		LOG.error(exception.getMessage(), exception);
	}

	@Around(EXECUTION)
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Signature signature = proceedingJoinPoint.getSignature();
		if (signature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) signature;
			Class<?> returnType = methodSignature.getReturnType();
			if (ModelAndView.class.isAssignableFrom(returnType)) {
				return proceedingJoinPoint.proceed();
			}
		}
		try {
			return proceedingJoinPoint.proceed();
		} catch (BusinessException be) {
			WebResult webResult = new WebResult();
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(be.getMessage());
			return webResult;
		}
	}
	
}
