package org.platform.modules.user.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.user.dao.RoleResourceDAO;
import org.platform.modules.user.entity.RoleResource;
import org.platform.modules.user.service.IRoleResourceService;

@Service("roleResourceService")
public class RoleResourceServiceImpl extends GenericServiceImpl<RoleResource, Long> implements IRoleResourceService {

	@Resource(name = "roleResourceDAO")
	private RoleResourceDAO roleResourceDAO = null;

	@Override
	public GenericDAO<RoleResource, Long> obtainDAOInstance() {
		return roleResourceDAO;
	}

}
