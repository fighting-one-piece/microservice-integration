package org.platform.modules.oauth.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.oauth.dao.RoleDAO;
import org.platform.modules.oauth.dao.UserRoleDAO;
import org.platform.modules.oauth.entity.Role;
import org.platform.modules.oauth.entity.UserRole;
import org.platform.modules.oauth.service.IRoleService;
import org.platform.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("roleService")
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements IRoleService {

	@Resource(name = "roleDAO")
	private RoleDAO roleDAO = null;
	
	@Resource(name = "userRoleDAO")
	private UserRoleDAO userRoleDAO = null;
	
	@Override
	public GenericDAO<Role, Long> obtainDAOInstance() {
		return roleDAO;
	}

	@Override
	public List<Role> readRolesByUserId(Long userId) throws BusinessException {
		if (null == userId) throw new BusinessException(ResultCode.PARAM_NULL);
		Query query = new Query();
		query.addCondition("userId", userId);
		List<UserRole> urs = userRoleDAO.readDataListByCondition(query);
		List<Role> roles = new ArrayList<Role>();
		for (int i = 0, len = urs.size(); i < len; i++) {
			Role role = roleDAO.readDataByPK(urs.get(i).getRoleId());
			if (null != role) roles.add(role);
		}
		return roles;
	}
	
}
