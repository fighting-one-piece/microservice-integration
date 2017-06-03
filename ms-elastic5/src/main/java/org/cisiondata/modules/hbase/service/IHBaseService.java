package org.cisiondata.modules.hbase.service;

import java.util.Map;

import org.cisiondata.utils.exception.BusinessException;

public interface IHBaseService {

	/**
	 * 根据table和id读取数据
	 * @param table
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> readDataById(String table, String id) throws BusinessException;
	
}
