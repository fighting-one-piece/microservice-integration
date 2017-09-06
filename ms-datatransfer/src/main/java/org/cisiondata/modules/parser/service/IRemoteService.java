package org.cisiondata.modules.parser.service;

import java.util.Map;

import org.cisiondata.utils.exception.BusinessException;

public interface IRemoteService {

	public Object invoke(String url, Map<String, String> params) throws BusinessException;
	
}
