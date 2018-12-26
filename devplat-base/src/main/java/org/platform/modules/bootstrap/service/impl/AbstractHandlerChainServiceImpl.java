package org.platform.modules.bootstrap.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.platform.modules.bootstrap.service.IHandlerChainService;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandlerChainServiceImpl implements IHandlerChainService {

	protected Logger LOG = LoggerFactory.getLogger(AbstractHandlerChainServiceImpl.class);
	
	@Override
	public Object[] preHandle(HttpServletRequest request) throws BusinessException {
		return new Object[]{true};
	}

	@Override
	public Object[] postHandle(HttpServletRequest request, Object result) throws BusinessException {
		return null;
	}

}
