package org.platform.modules.oauth.service;

import java.util.List;

import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.oauth.entity.Role;
import org.platform.utils.exception.BusinessException;

public interface IRoleService extends IGenericService<Role, Long> {
	
	/**
	 * 根据用户ID读取角色列表
	 * @param userId
	 * @return
	 * @throws BusinessException
	 */
	public List<Role> readRolesByUserId(Long userId) throws BusinessException;

}
