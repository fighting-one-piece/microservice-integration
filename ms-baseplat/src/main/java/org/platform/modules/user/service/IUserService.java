package org.platform.modules.user.service;

import java.util.List;

import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.user.entity.User;
import org.platform.modules.user.entity.UserAttribute;
import org.platform.modules.user.entity.UserPO;
import org.platform.utils.exception.BusinessException;

public interface IUserService extends IGenericService<User, Long> {
	
	/**
	 * 新增用户属性信息
	 * @param userId
	 * @param key
	 * @param value
	 * @throws BusinessException
	 */
	public void insertUserAttribute(Long userId, String key, Object value) throws BusinessException;
	
	/**
	 * 更新用户属性信息
	 * @param userId
	 * @param key
	 * @param value
	 * @throws BusinessException
	 */
	public void updateUserAttribute(Long userId, String key, Object value) throws BusinessException;
	
	/**
	 * 更新用户缓存属性信息
	 * @param userId
	 * @param key
	 * @param value
	 * @throws BusinessException
	 */
	public void updateUserCacheAttribute(Long userId, String key, Object value) throws BusinessException;
	
	/**
	 * 根据ID读取用户信息
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public UserPO readUserById(Long id) throws BusinessException;
	
	/**
	 * 根据ACCOUNT读取用户信息
	 * @param account
	 * @return
	 * @throws BusinessException
	 */
	public UserPO readUserByAccount(String account) throws BusinessException;
	
	/**
	 * 根据ACCESSID读取用户信息
	 * @param accessId
	 * @return
	 * @throws BusinessException
	 */
	public UserPO readUserByAccessId(String accessId) throws BusinessException;
	
	/**
	 * 根据MOBILEPHONE读取用户信息
	 * @param mobilePhone
	 * @return
	 * @throws BusinessException
	 */
	public UserPO readUserByMobilePhone(String mobilePhone) throws BusinessException;
	
	/**
	 * 根据ACCOUNT和PASSWORD读取用户信息
	 * @param account
	 * @param password
	 * @return
	 * @throws BusinessException
	 */
	public UserPO readUserByAccountAndPassword(String account, String password) throws BusinessException;
	
	/**
	 * 根据条件读取用户属性
	 * @param userId
	 * @param key
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public UserAttribute readUserAttribute(Long userId, String key, Object value) throws BusinessException;
	
	/**
	 * 根据条件读取用户属性列表
	 * @param userId
	 * @param key
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public List<UserAttribute> readUserAttributeList(Long userId, String key, Object value) throws BusinessException;
	
	/**
	 * 根据ACCOUNT删除用户缓存信息
	 * @param account
	 * @throws BusinessException
	 */
	public void deleteUserCacheByAccount(String account) throws BusinessException;
	
}
