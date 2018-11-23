package org.platform.modules.oauth.service;

import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.oauth.entity.Client;
import org.platform.utils.exception.BusinessException;

public interface IClientService extends IGenericService<Client, Long> {
	
	/**
	 * 根据clientId读取client信息
	 * @param clientId
	 * @return
	 * @throws BusinessException
	 */
	public Client readClientByClientId(String clientId) throws BusinessException;

}
