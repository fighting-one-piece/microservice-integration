package org.platform.modules.oauth.service;

import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.oauth.entity.User;
import org.platform.utils.exception.BusinessException;

public interface IUserService extends IGenericService<User, Long> {
	
	/**
	 * 根据用户名读取用户信息
	 * @param username
	 * @return
	 * @throws BusinessException
	 */
	public User readUserByUsername(String username) throws BusinessException;
	
	/**
	 * 根据用户名和密码读取用户信息
	 * @param username
	 * @param password
	 * @return
	 * @throws BusinessException
	 */
	public User readUserByUsernameAndPassword(String username, String password) throws BusinessException;
	
}
