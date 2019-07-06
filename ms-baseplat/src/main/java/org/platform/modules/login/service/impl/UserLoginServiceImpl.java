package org.platform.modules.login.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.login.Constants;
import org.platform.modules.login.service.IUserLoginService;
import org.platform.modules.login.web.WebContext;
import org.platform.modules.login.web.WebUtils;
import org.platform.modules.user.entity.User;
import org.platform.modules.user.entity.UserPO;
import org.platform.modules.user.service.IUserBizService;
import org.platform.utils.date.DateFormatter;
import org.platform.utils.endecrypt.Base64Utils;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.json.GsonUtils;
import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.token.TokenUtils;
import org.platform.utils.web.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("userLoginService")
public class UserLoginServiceImpl implements IUserLoginService {

	private Logger LOG = LoggerFactory.getLogger(UserLoginServiceImpl.class);
	
	private SimpleDateFormat DF = DateFormatter.DATE.get();
	private SimpleDateFormat TF = DateFormatter.TIME.get();
	
	@Resource(name = "userBizService")
	private IUserBizService userBizService = null;
	
	@Override
	public UserPO login(String account) throws BusinessException {
		return userBizService.readUserByAccount(account);
	}

	@Override
	public Map<String, Object> login(String account, String password, String uuid, String verificationCode) throws BusinessException {
		LOG.info("account:{} password:{} uuid:{} verificationCode:{}", account, password, uuid, verificationCode);
		if (StringUtils.isBlank(account) || StringUtils.isBlank(verificationCode)) 
			throw new BusinessException(ResultCode.PARAM_NULL);
		UserPO userPO = null;
		if (StringUtils.isNotBlank(password) && StringUtils.isNotBlank(uuid)) {
			userPO = loginByPassword(account, password, uuid, verificationCode);
		} else {
			userPO = loginBySms(account, verificationCode);
		}
		String sessionId = WebContext.get().getSession().getId();
		userPO.setSessionId(sessionId);
		userBizService.updateUserCacheAttribute(userPO.getId(), User.ATTRIBUTE.SESSION_ID, sessionId);
		String ip = IPUtils.getIPAddress(WebContext.get().getRequest());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userPO.getId());
		params.put("sessionId", sessionId);
		String accessToken = TokenUtils.genNAuthenticationMD5Token(1, account, userPO.getPassword(), 
			ip, DF.format(new Date())) + Base64Utils.encode(GsonUtils.fromMapToJson(params));
		userPO.setAccessToken(accessToken);
		userBizService.updateUserCacheAttribute(userPO.getId(), User.ATTRIBUTE.ACCESS_TOKEN, accessToken);
		WebContext.get().getSession().getSessionManager().setCookieSecure(true);
		WebContext.get().getSession().setAttribute(Constants.SESSION_CURRENT_USER, userPO);
		WebContext.get().getSession().setAttribute(Constants.SESSION_CURRENT_USER_ACCOUNT, account);
		RedisClusterUtils.getInstance().set(accessToken, sessionId, 1800);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("account", account);
		result.put("nickName", userPO.getNickName());
		result.put("realName", userPO.getRealName());
		result.put("mobilePhone", userPO.getMobilePhone());
		result.put("email", userPO.getEmail());
		result.put("createTime", TF.format(userPO.getCreateTime()));
		result.put("expireTime", TF.format(userPO.getExpireTime()));
		result.put("accessToken", accessToken);
		return result;
	}

	@Override
	public void logout() throws BusinessException {
		userBizService.deleteUserCacheByAccount(WebUtils.getCurrentUserAccout());
	}
	
	/** 账号密码登录 */
	private UserPO loginByPassword(String account, String password, String uuid, String verificationCode) {
		if (StringUtils.isBlank(password)) throw new BusinessException(ResultCode.PARAM_NULL);
		validateVerificationCode(uuid, verificationCode);
		return userBizService.readUserByAccountAndPassword(account, password);
	}
	
	/** 账号短信登录 */
	private UserPO loginBySms(String account, String verificationCode) {
		validateSmsVerificationCode(account, verificationCode);
		return userBizService.readUserByAccount(account);
	}
	
	private void validateVerificationCode(String uuid, String verificationCode) throws BusinessException {
		if (StringUtils.isBlank(uuid) || StringUtils.isBlank(verificationCode)) 
			throw new BusinessException(ResultCode.VERIFICATION_CODE_FAILURE);
		try {
			Object verificationCodeObj = RedisClusterUtils.getInstance().get(uuid);
			if (null == verificationCodeObj) throw new BusinessException(ResultCode.VERIFICATION_CODE_FAILURE);
			verificationCode = verificationCode.toLowerCase();
			LOG.info("vc : {} rvc:  {}", verificationCode, String.valueOf(verificationCodeObj).toLowerCase());
			if (!verificationCode.equalsIgnoreCase(String.valueOf(verificationCodeObj).toLowerCase())){
				throw new BusinessException(ResultCode.VERIFICATION_CODE_FAILURE);
			}
			RedisClusterUtils.getInstance().delete(uuid);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(ResultCode.VERIFICATION_CODE_FAILURE);
		}
	}
	
	private void validateSmsVerificationCode(String account, String verificationCode) throws BusinessException {
		
	}
	
}
