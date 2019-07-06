package org.platform.modules.login.service;

import java.util.Map;

import org.platform.modules.user.entity.UserPO;
import org.platform.utils.exception.BusinessException;

public interface IUserLoginService {
	
	/**
	 * 用户登录
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	public UserPO login(String account) throws BusinessException;

	/**
	 * 用户登录
	 * @param account
	 * @param password
	 * @param uuid
	 * @param verificationCode
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> login(String account, String password, String uuid, String verificationCode) throws BusinessException;

	/**
	 * 用户登出
	 */
	public void logout() throws BusinessException;

}
