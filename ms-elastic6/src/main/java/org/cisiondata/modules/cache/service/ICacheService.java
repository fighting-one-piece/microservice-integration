package org.cisiondata.modules.cache.service;

import java.util.Set;

import org.cisiondata.utils.exception.BusinessException;

public interface ICacheService {
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public Object readKey(String key) throws BusinessException;

	/**
	 * 
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	public Set<String> readKeys(String pattern) throws BusinessException;
	
	/**
	 * 
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	public int deleteKeys(String pattern) throws BusinessException;
	
}
