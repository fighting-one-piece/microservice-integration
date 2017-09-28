package org.cisiondata.modules.elastic.service.rmi;

import org.cisiondata.utils.exception.BusinessException;

public interface IElasticRmiService {

	public Object readDataList() throws BusinessException;
	
}
