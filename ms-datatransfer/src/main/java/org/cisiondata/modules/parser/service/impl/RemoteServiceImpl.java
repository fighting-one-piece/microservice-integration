package org.cisiondata.modules.parser.service.impl;

import java.util.Map;

import org.cisiondata.modules.parser.service.IRemoteService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.http.HttpUtils;
import org.springframework.stereotype.Service;

@Service("remoteService")
public class RemoteServiceImpl implements IRemoteService {

	@Override
	public Object invoke(String url, Map<String, String> params) throws BusinessException {
		return HttpUtils.sendGet(url, params);
	}
	
}
