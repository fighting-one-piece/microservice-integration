package org.platform.modules.login.web.session;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 会话管理器 */
@Component("sessionManager")
public class SessionManager {

	private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
	
	public static final String REQUEST_SESSION_ATTRIBUTE_NAME = "_session";
	
	@Resource(name = "defaultCookieHandler")
	private CookieHandler cookieHandler = null;

	@Resource(name = "redisStorageHandler")
	private StorageHandler storageHandler = null;

	public SessionManager() {
		this.cookieHandler = new DefaultCookieHandler();
	}

	public SessionManager(StorageHandler storageHandler) {
		this.cookieHandler = new DefaultCookieHandler();
		this.storageHandler = storageHandler;
	}

	public SessionManager(CookieHandler cookieHandler, StorageHandler storageHandler) {
		this.cookieHandler = cookieHandler;
		this.storageHandler = storageHandler;
	}

	/** 获取当前请求的会话, 如果没有则创建 */
	public Session getSession(HttpServletRequest request, HttpServletResponse response) {
		return getSession(request, response, true);
	}

	/** 获取当前请求的会话, 如果没有且create=true时创建 */
	public Session getSession(HttpServletRequest request, HttpServletResponse response, boolean create) throws SessionException {
		try {
			Session session = (Session) request.getAttribute(REQUEST_SESSION_ATTRIBUTE_NAME);
			if (session == null) {
				String sessionId = cookieHandler.getSessionId(request, response);
				if (sessionId == null && create) {
					sessionId = storageHandler.createSessionId(request, response);
					if (sessionId == null) {
						throw new SessionException("create session id fail.");
					} else {
						cookieHandler.setSessionId(sessionId, request, response);
					}
				}
				if (sessionId != null) {
					session = new Session(this, sessionId, request, response);
					storageHandler.initialize(sessionId);
					response.addHeader("Cache-Control", "private");
					request.setAttribute(REQUEST_SESSION_ATTRIBUTE_NAME, session);
				}
			}
			return session;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new SessionException(e);
		}
	}

	public CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	public void setCookieHandler(CookieHandler cookieHandler) {
		this.cookieHandler = cookieHandler;
	}

	public StorageHandler getStorageHandler() {
		return storageHandler;
	}

	public void setStorageHandler(StorageHandler storageHandler) {
		this.storageHandler = storageHandler;
	}

	public void setCookieName(String cookieName) {
		cookieHandler.setCookieName(cookieName);
	}

	public void setCookieDomain(String cookieDomain) {
		cookieHandler.setCookieDomain(cookieDomain);
	}

	public void setCookiePath(String cookiePath) {
		cookieHandler.setCookiePath(cookiePath);
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		cookieHandler.setCookieMaxAge(cookieMaxAge);
	}
	
	public void setCookieSecure(boolean cookieSecure) {
		cookieHandler.setCookieSecure(cookieSecure);;
	}
	
}
