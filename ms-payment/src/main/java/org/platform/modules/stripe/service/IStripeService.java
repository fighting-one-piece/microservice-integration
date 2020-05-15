package org.platform.modules.stripe.service;

import java.util.Map;

import org.platform.utils.exception.BusinessException;

public interface IStripeService {

	/**
	 * 
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> insertSource(Map<String, String> params) throws BusinessException;
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws BusinessException
	 */
	public Object insertNotify(Map<String, String> params) throws BusinessException;
	
}
