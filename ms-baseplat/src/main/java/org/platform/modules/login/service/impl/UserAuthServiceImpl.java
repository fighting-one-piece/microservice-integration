package org.platform.modules.login.service.impl;

import org.platform.modules.login.service.IUserAuthService;
import org.platform.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("userAuthService")
public class UserAuthServiceImpl implements IUserAuthService {

	@Override
	public boolean readResourceAuthorization(String requestUrl) throws BusinessException {
		return false;
	}

}
