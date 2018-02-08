package org.cisiondata.modules.oauth.service;

import org.cisiondata.modules.abstr.service.IGenericService;
import org.cisiondata.modules.oauth.entity.Client;
import org.cisiondata.utils.exception.BusinessException;

public interface IClientService extends IGenericService<Client, Long> {
	
	/**
	 * 根据clientId读取client信息
	 * @param clientId
	 * @return
	 * @throws BusinessException
	 */
	public Client readClientByClientId(String clientId) throws BusinessException;

}
