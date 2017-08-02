package org.cisiondata.modules.elastic.service;

import org.cisiondata.utils.exception.BusinessException;

public interface IElasticService {
	
	public Object readDataList(String indices, String types, String keywords, int highLight,
			Integer currentPageNum, Integer rowNumPerPage) throws BusinessException;

}
