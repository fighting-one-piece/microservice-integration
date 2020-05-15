package org.platform.utils.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.platform.modules.abstr.entity.ResultCode;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DAOLayerAspect {

	private Logger LOG = LoggerFactory.getLogger(DAOLayerAspect.class);

	private static final String EXECUTION = "execution(* org.platform.modules.*.dao.*.*(..))";
	
	/**
	@Before(EXECUTION)
	public void logBefore(JoinPoint joinPoint){
	}

	@After(EXECUTION)
	public void logAfter(JoinPoint joinPoint){
	}

	@AfterReturning(pointcut = EXECUTION, returning = "result")
	public void logAfterReturn(JoinPoint joinPoint, Object result) {
	}

	@AfterThrowing(pointcut = EXECUTION, throwing = "exception")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable exception){
		if (exception instanceof DataException || exception instanceof DataAccessException) {
			throw new BusinessException(ResultCode.DATABASE_OPERATION_FAIL);
		}
		LOG.error(exception.getMessage(), exception);
	}
	*/

	@Around(EXECUTION)
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object result = null;
		try {
			result = proceedingJoinPoint.proceed();
		} catch (DuplicateKeyException dke) {
			LOG.error(dke.getMessage(), dke);
			throw new BusinessException(ResultCode.DATA_EXISTED);
		} catch (DataException | DataAccessException de) {
			LOG.error(de.getMessage(), de);
			throw new BusinessException(ResultCode.DATABASE_OPERATION_FAIL);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
		return result;
	}
	
}
