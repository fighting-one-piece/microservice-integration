package org.platform.modules.bootstrap.service;

import javax.servlet.http.HttpServletRequest;

import org.platform.utils.exception.BusinessException;

public interface IHandlerChainService {

	/**
	 * 请求预处理
	 * @param request
	 * @return 第一个参数为true or false 
	 * 		       第二个参数为异常消息
	 * @throws BusinessException
	 */
	public Object[] preHandle(HttpServletRequest request) throws BusinessException;
	
	/**
	 * 请求后结果处理
	 * @param request
	 * @param result
	 * @return 
	 * @throws BusinessException
	 */
	public Object[] postHandle(HttpServletRequest request, Object result) throws BusinessException;
	
}
