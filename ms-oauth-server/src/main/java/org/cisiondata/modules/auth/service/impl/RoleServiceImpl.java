package org.cisiondata.modules.auth.service.impl;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.abstr.service.impl.GenericServiceImpl;
import org.cisiondata.modules.auth.dao.RoleDAO;
import org.cisiondata.modules.auth.entity.Role;
import org.cisiondata.modules.auth.service.IRoleService;
import org.springframework.stereotype.Service;

@Service("roleService")
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements IRoleService {

	@Resource(name = "roleDAO")
	private RoleDAO roleDAO = null;
	
	@Override
	public GenericDAO<Role, Long> obtainDAOInstance() {
		return roleDAO;
	}

}
