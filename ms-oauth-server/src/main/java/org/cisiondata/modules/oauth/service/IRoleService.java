package org.cisiondata.modules.oauth.service;

import java.util.List;

import org.cisiondata.modules.abstr.service.IGenericService;
import org.cisiondata.modules.oauth.entity.Role;
import org.cisiondata.utils.exception.BusinessException;

public interface IRoleService extends IGenericService<Role, Long> {
	
	/**
	 * 根据用户ID读取角色列表
	 * @param userId
	 * @return
	 * @throws BusinessException
	 */
	public List<Role> readRolesByUserId(Long userId) throws BusinessException;

}
