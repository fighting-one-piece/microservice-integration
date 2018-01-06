package org.cisiondata.modules.cache.service;

import java.util.Set;

public interface ICacheService {
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	public Object readKey(String key) throws RuntimeException;

	/**
	 * 
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	public Set<String> readKeys(String pattern) throws RuntimeException;
	
	/**
	 * 
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	public int deleteKeys(String pattern) throws RuntimeException;
	
}
