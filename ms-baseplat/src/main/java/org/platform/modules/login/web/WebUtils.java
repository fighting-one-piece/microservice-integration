package org.platform.modules.login.web;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.login.Constants;
import org.platform.modules.login.service.IUserLoginService;
import org.platform.modules.login.web.session.SessionManager;
import org.platform.modules.user.entity.UserPO;
import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.spring.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {

	private static Logger LOG = LoggerFactory.getLogger(WebUtils.class);

	public static String getCurrentUserAccout() {
		UserPO userPO = getCurrentUser();
		return null != userPO ? userPO.getAccount() : null;
	}

	public static UserPO getCurrentUser() {
		UserPO userPO = null;
		try {
			WebContext webContext = WebContext.get();
			if (null != webContext && null != webContext.getSession()) {
				userPO = webContext.getSession().getAttribute(Constants.SESSION_CURRENT_USER);
			}
			if (null == userPO) {
				String accessToken = getAccessTokenFromHeaders();
				if (!StringUtils.isBlank(accessToken)) {
					String sessionId = (String) RedisClusterUtils.getInstance().get(accessToken);
					if (!StringUtils.isBlank(sessionId)) {
						SessionManager sessionManager = SpringBeanFactory.getBean(SessionManager.class);
						Object accountObject = sessionManager.getStorageHandler().getAttribute(sessionId,
							webContext.getRequest(), webContext.getResponse(), Constants.SESSION_CURRENT_USER_ACCOUNT);
						LOG.info("WebUtils Account: {}", accountObject);
						if (null != accountObject) {
							IUserLoginService userLoginService = SpringBeanFactory.getBean(IUserLoginService.class);
							userPO = userLoginService.login((String) accountObject);
						}
					}
				}
			}
			setCurrentUser(userPO);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
		return userPO;
	}

	public static void setCurrentUser(UserPO userPO) {
		if (null == userPO) return;
		WebContext webContext = WebContext.get();
		if (null != webContext && null != webContext.getSession()) {
			webContext.getSession().setAttribute(Constants.SESSION_CURRENT_USER, userPO);
			webContext.getSession().setAttribute(Constants.SESSION_CURRENT_USER_ACCOUNT, userPO.getAccount());
		}
	}

	public static void removeCurrentUser() {
		WebContext webContext = WebContext.get();
		if (null != webContext && null != webContext.getSession()) {
			webContext.getSession().removeAttribute(Constants.SESSION_CURRENT_USER);
			webContext.getSession().removeAttribute(Constants.SESSION_CURRENT_USER_ACCOUNT);
		}
	}

	public static String getAccessTokenFromHeaders() {
		return null == WebContext.get() || null == WebContext.get().getRequest() ? null
			: WebContext.get().getRequest().getHeader("accessToken");
	}

	public static String getAccountFromHeaders() {
		return getCookieValueFromHeaders(Constants.COOKIE_USER_ACCOUNT);
	}

	public static String getCookieValueFromHeaders(String cookieKey) {
		String cookieValue = "";
		String cookieInfo = WebContext.get().getRequest().getHeader("Cookie");
		String[] cookies = cookieInfo.split(";");
		for (int i = 0, len = cookies.length; i < len; i++) {
			String cookie = cookies[i];
			if (cookie.indexOf("=") != -1) {
				String[] cookieKV = cookie.split("=");
				if (cookieKey.equals(cookieKV[0].trim())) {
					cookieValue = cookieKV.length == 2 ? cookieKV[1] : "";
				}
			}
		}
		return cookieValue;
	}
	
}
