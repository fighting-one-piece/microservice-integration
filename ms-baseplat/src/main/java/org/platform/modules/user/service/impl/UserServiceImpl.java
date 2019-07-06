package org.platform.modules.user.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.service.converter.IConverter;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.abstr.utils.EntityAttributeUtils;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.user.dao.UserAttributeDAO;
import org.platform.modules.user.dao.UserDAO;
import org.platform.modules.user.entity.User;
import org.platform.modules.user.entity.UserAttribute;
import org.platform.modules.user.entity.UserPO;
import org.platform.modules.user.service.IUserService;
import org.platform.modules.user.service.converter.UserConverter;
import org.platform.utils.cache.CacheKey;
import org.platform.utils.cache.CacheUtils;
import org.platform.utils.endecrypt.EndecryptUtils;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.regex.RegexUtils;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl extends GenericServiceImpl<User, Long> implements IUserService {
	
	@Resource(name = "userDAO")
	private UserDAO userDAO = null;

	@Resource(name = "userAttributeDAO")
	private UserAttributeDAO userAttributeDAO = null;
	
	@Override
	public GenericDAO<User, Long> obtainDAOInstance() {
		return userDAO;
	}
	
	@Override
	protected IConverter<?, ?> obtainConverter() {
		return UserConverter.getInstance();
	}
	
	@Override
	public void insertUserAttribute(Long userId, String key, Object value) throws BusinessException {
		UserAttribute userAttribute = new UserAttribute();
		userAttribute.setUserId(userId);
		userAttribute.setKey(key);
		String[] valueAndKey = EntityAttributeUtils.extractValueAndKind(value);
		userAttribute.setValue(valueAndKey[0]);
		userAttribute.setType(valueAndKey[1]);
		userAttributeDAO.insert(userAttribute);
		RedisClusterUtils.getInstance().hset(genIdCacheKey(userId), key, value);
	}
	
	@Override
	public void updateUserAttribute(Long userId, String key, Object value) throws BusinessException {
		UserAttribute userAttribute = new UserAttribute();
		userAttribute.setUserId(userId);
		userAttribute.setKey(key);
		String[] valueAndKey = EntityAttributeUtils.extractValueAndKind(value);
		userAttribute.setValue(valueAndKey[0]);
		userAttribute.setType(valueAndKey[1]);
		userAttributeDAO.update(userAttribute);
		RedisClusterUtils.getInstance().hset(genIdCacheKey(userId), key, value);
	}
	
	@Override
	public void updateUserCacheAttribute(Long userId, String key, Object value) throws BusinessException {
		RedisClusterUtils.getInstance().hset(genIdCacheKey(userId), key, value);
	}
	
	@Override
	public UserPO readUserById(Long id) throws BusinessException {
		if (null == id) throw new BusinessException(ResultCode.PARAM_NULL);
		String idCacheKey = genIdCacheKey(id);
		Object userPOObj = RedisClusterUtils.getInstance().hgetBean(idCacheKey, UserPO.class);
		if (null != userPOObj) return (UserPO) userPOObj;
		User user = userDAO.readDataByPK(id);
		if (null == user || user.hasDeleted()) throw new BusinessException(ResultCode.ACCOUNT_NOT_EXIST);
		UserPO userPO = (UserPO) obtainConverter().convertObject(user);
		initializingUserCache(userPO);
		return userPO;
	}
	
	@Override
	public UserPO readUserByAccount(String account) throws BusinessException {
		if (StringUtils.isBlank(account)) throw new BusinessException(ResultCode.PARAM_NULL);
		boolean isMobilePhone = RegexUtils.isMobilePhone(account);
		String cacheKey = isMobilePhone ? genMobilePhoneCacheKey(account) : genAccountCacheKey(account);
		Object idObj = RedisClusterUtils.getInstance().get(cacheKey);
		if (null != idObj) return readUserById((Long) idObj);
		Query query = new Query();
		query.addCondition("deleteFlag", false);
		query.addCondition(isMobilePhone ? "mobilePhone" : "account", account);
		User user = userDAO.readDataByCondition(query);
		if (null == user) throw new BusinessException(ResultCode.ACCOUNT_NOT_EXIST);
		UserPO userPO = (UserPO) obtainConverter().convertObject(user);
		initializingUserCache(userPO);
		return userPO;
	}
	
	@Override
	public UserPO readUserByAccessId(String accessId) throws BusinessException {
		if (null == accessId) throw new BusinessException(ResultCode.PARAM_NULL);
		String accessIdCacheKey = genAccessIdCacheKey(accessId);
		Object idObj = RedisClusterUtils.getInstance().get(accessIdCacheKey);
		if (null != idObj) return readUserById((Long) idObj);
		Query query = new Query();
		query.addCondition("key", "accessId");
		query.addCondition("value", accessId);
		UserAttribute userAttribute = userAttributeDAO.readDataByCondition(query);
		if (null == userAttribute) throw new BusinessException(ResultCode.ACCOUNT_NOT_EXIST);
		return readUserById(userAttribute.getUserId());
	}
	
	@Override
	public UserPO readUserByMobilePhone(String mobilePhone) throws BusinessException {
		if (StringUtils.isBlank(mobilePhone)) throw new BusinessException(ResultCode.PARAM_NULL);
		String mobilePhoneCacheKey = genMobilePhoneCacheKey(mobilePhone);
		Object idObj = RedisClusterUtils.getInstance().get(mobilePhoneCacheKey);
		if (null != idObj) return readUserById((Long) idObj);
		Query query = new Query();
		query.addCondition("deleteFlag", false);
		query.addCondition("mobilePhone", mobilePhone);
		User user = userDAO.readDataByCondition(query);
		if (null == user) throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
		UserPO userPO = (UserPO) obtainConverter().convertObject(user);
		initializingUserCache(userPO);
		return userPO;
	}
	
	@Override
	public UserPO readUserByAccountAndPassword(String account, String password) throws BusinessException {
		if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
			throw new BusinessException(ResultCode.PARAM_NULL);
		}
		UserPO userPO = readUserByAccount(account);
		if (null == userPO) throw new BusinessException(ResultCode.ACCOUNT_NOT_EXIST);
		String encryptPassword = EndecryptUtils.encryptPassword(password, userPO.getSalt());
		if (!encryptPassword.equals(userPO.getPassword())) {
			throw new BusinessException(ResultCode.ACCOUNT_PASSWORD_NOT_MATCH);
		}
		return userPO;
	}
	
	@Override
	public UserAttribute readUserAttribute(Long userId, String key, Object value) throws BusinessException {
		Query query = new Query();
		if (null != userId) query.addCondition("userId", userId);
		if (StringUtils.isNotBlank(key)) query.addCondition("key", key);
		if (null != value) query.addCondition("value", String.valueOf(value));
		return userAttributeDAO.readDataByCondition(query);
	}
	
	@Override
	public List<UserAttribute> readUserAttributeList(Long userId, String key, Object value) throws BusinessException {
		Query query = new Query();
		if (null != userId) query.addCondition("userId", userId);
		if (StringUtils.isNotBlank(key)) query.addCondition("key", key);
		if (null != value) query.addCondition("value", String.valueOf(value));
		return userAttributeDAO.readDataListByCondition(query);
	}
	
	@Override
	public void deleteUserCacheByAccount(String account) throws BusinessException {
		if (StringUtils.isBlank(account)) return;
		UserPO userPO = readUserByAccount(account);
		if (null == userPO) return;
		RedisClusterUtils.getInstance().delete(userPO.getAccessToken());
		RedisClusterUtils.getInstance().delete(genIdCacheKey(userPO.getId()));
		RedisClusterUtils.getInstance().delete(genAccountCacheKey(userPO.getAccount()));
		RedisClusterUtils.getInstance().delete(genAccessIdCacheKey(userPO.getAccessId()));
		RedisClusterUtils.getInstance().delete(genMobilePhoneCacheKey(userPO.getMobilePhone()));
	}
	
	private String genIdCacheKey(Long id) {
		return CacheUtils.genCacheKey(CacheKey.USER.ID, id);
	}
	
	private String genAccountCacheKey(String account) {
		return CacheUtils.genCacheKey(CacheKey.USER.ACCOUNT, account);
	}
	
	private String genAccessIdCacheKey(String accessId) {
		return CacheUtils.genCacheKey(CacheKey.USER.ACCESSID, accessId);
	}
	
	private String genMobilePhoneCacheKey(String mobilePhone) {
		return CacheUtils.genCacheKey(CacheKey.USER.MOBILEPHONE, mobilePhone);
	}
	
	private void initializingUserCache(UserPO userPO) {
		Long id = userPO.getId();
		String mobilePhone = userPO.getMobilePhone();
		RedisClusterUtils.getInstance().hsetBean(genIdCacheKey(id), userPO, 1800);
		RedisClusterUtils.getInstance().set(genAccountCacheKey(userPO.getAccount()), id);
		RedisClusterUtils.getInstance().set(genAccessIdCacheKey(userPO.getAccessId()), id);
		RedisClusterUtils.getInstance().set(genMobilePhoneCacheKey(mobilePhone), id);
	}
	
}
