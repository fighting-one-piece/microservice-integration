package org.cisiondata.modules.elastic.service.rmi;

import org.cisiondata.utils.exception.BusinessException;

import com.alibaba.dubbo.config.annotation.Service;

@Service(interfaceClass = IElasticRmiService.class, timeout = 5000)
public class ElasticRmiServiceImpl implements IElasticRmiService {

	@Override
	public Object readDataList() throws BusinessException {
		return null;
	}

}
