package org.platform.modules.elastic.service.rmi;

import org.platform.utils.exception.BusinessException;

import com.alibaba.dubbo.config.annotation.Service;

@Service(interfaceClass = IElasticRmiService.class, timeout = 5000)
public class ElasticRmiServiceImpl implements IElasticRmiService {

	@Override
	public Object readDataList() throws BusinessException {
		return null;
	}

}
