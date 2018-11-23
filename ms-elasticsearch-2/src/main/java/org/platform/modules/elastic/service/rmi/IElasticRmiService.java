package org.platform.modules.elastic.service.rmi;

import org.platform.utils.exception.BusinessException;

public interface IElasticRmiService {

	public Object readDataList() throws BusinessException;
	
}
