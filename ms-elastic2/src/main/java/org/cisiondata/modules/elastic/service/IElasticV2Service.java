package org.cisiondata.modules.elastic.service;

import java.util.Map;

import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.utils.exception.BusinessException;

public interface IElasticV2Service {
	
	/**
	 * 查询指定字段数据
	 * @param indices
	 * @param types
	 * @param fields
	 * @param keywords
	 * @param highLight
	 * @return
	 * @throws RuntimeException
	 */
	public Object readDataList(String indices, String types, String fields, String keywords, 
			int highLight, Integer currentPageNum, Integer rowNumPerPage) throws BusinessException;
	
	/**
	 * 查询数据
	 * @param indices
	 * @param types
	 * @param fields
	 * @param keywords
	 * @param highLight
	 * @param scrollId
	 * @return
	 * @throws RuntimeException
	 */
	public QueryResult<Map<String, Object>> readDataList(String indices, String types, 
			String fields, String keywords, int highLight, String scrollId) throws BusinessException;

}
