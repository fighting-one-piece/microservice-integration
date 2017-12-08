package org.cisiondata.modules.elastic.service;

import java.util.Map;

import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.utils.exception.BusinessException;

public interface IElasticV3Service {
	
	/**
	 * 全文查询数据
	 * @param keywords
	 * @return
	 * @throws RuntimeException
	 */
	public QueryResult<Map<String, Object>> readDataList(String keywords) throws BusinessException;
	
}
