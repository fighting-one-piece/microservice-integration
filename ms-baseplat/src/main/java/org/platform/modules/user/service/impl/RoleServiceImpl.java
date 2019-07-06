package org.platform.modules.user.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.user.dao.RoleDAO;
import org.platform.modules.user.entity.Role;
import org.platform.modules.user.service.IRoleService;

@Service("roleService")
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements IRoleService {

	@Resource(name = "roleDAO")
	private RoleDAO roleDAO = null;

	@Override
	public GenericDAO<Role, Long> obtainDAOInstance() {
		return roleDAO;
	}

}
