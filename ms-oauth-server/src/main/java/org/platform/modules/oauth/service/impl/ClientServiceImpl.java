package org.platform.modules.oauth.service.impl;

import javax.annotation.Resource;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.oauth.dao.ClientDAO;
import org.platform.modules.oauth.entity.Client;
import org.platform.modules.oauth.service.IClientService;
import org.platform.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("clientService")
public class ClientServiceImpl extends GenericServiceImpl<Client, Long> implements IClientService {

	@Resource(name = "clientDAO")
	private ClientDAO clientDAO = null;
	
	@Override
	public GenericDAO<Client, Long> obtainDAOInstance() {
		return clientDAO;
	}
	
	@Override
	protected void preHandle(Object object) throws BusinessException {
		if (object instanceof Client) {
			Client client = (Client) object;
			Long id = client.getId();
			if (null != id) {
				Client dbClient = clientDAO.readDataByPK(id);
				if (null == dbClient) {
					throw new BusinessException("client not exists!");
				}
			}
		}
	}
	
	@Override
	public Client readClientByClientId(String clientId) throws BusinessException {
		if (null == clientId || "".equals(clientId)) throw new BusinessException(ResultCode.PARAM_NULL);
		Query query = new Query();
		query.addCondition("clientId", clientId);
		Client client = clientDAO.readDataByCondition(query);
		if (null == client) {
			throw new BusinessException("Client不存在!");
		}
		return client;
	}
	
	@SuppressWarnings("unused")
	private String genClientCacheKey(String clientId) {
		return "oauth:c:" + clientId;
	}
	
}
