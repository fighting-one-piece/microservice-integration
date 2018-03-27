package org.cisiondata.modules.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cisiondata.modules.abstr.web.WebResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
public class BusinessExceptionHandlerAdvice {

	private Logger LOG = LoggerFactory.getLogger(BusinessExceptionHandlerAdvice.class);

	/***
	 * 响应400错误
	 * 
	 * @param ex
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@ExceptionHandler(org.springframework.beans.TypeMismatchException.class)
	public WebResult handle400Exception(org.springframework.beans.TypeMismatchException ex, 
			HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebResult result = new WebResult();
		result.setCode(400);
		result.setFailure(ex.getMessage());
		LOG.error(ex.getMessage(), ex);
		return result;
	}

	/***
	 * 响应404 错误
	 * 
	 * @param ex
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 */
	@ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
	public WebResult handleNotFound404Exception(org.springframework.web.servlet.NoHandlerFoundException ex,
			HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebResult result = new WebResult();
		result.setCode(404);
		result.setFailure(ex.getMessage());
		LOG.error(ex.getMessage(), ex);
		return result;
	}

}
