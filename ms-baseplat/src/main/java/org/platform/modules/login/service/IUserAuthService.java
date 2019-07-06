package org.platform.modules.login.service;

import org.platform.utils.exception.BusinessException;

public interface IUserAuthService {
	
	/**
	 * 根据请求URL读取资源权限
	 * @param requestUrl
	 * @return
	 * @throws BusinessException
	 */
	public boolean readResourceAuthorization(String requestUrl) throws BusinessException;

}
