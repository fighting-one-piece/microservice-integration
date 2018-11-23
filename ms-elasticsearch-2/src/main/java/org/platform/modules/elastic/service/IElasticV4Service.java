package org.platform.modules.elastic.service;

import org.platform.utils.exception.BusinessException;

public interface IElasticV4Service {
	
	/**
	 * 查询SourceFile数据
	 * @param index
	 * @param type
	 * @param keyword
	 * @param highLight
	 * @return
	 * @throws RuntimeException
	 */
	public Object readDataList(String index, String type, String keyword, int deleteFlag) throws BusinessException;
	
}
