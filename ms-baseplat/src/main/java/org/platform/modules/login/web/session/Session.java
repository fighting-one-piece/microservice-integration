package org.platform.modules.login.web.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 会话 */
public class Session {

    private static final Logger LOG = LoggerFactory.getLogger(Session.class);
    
    private String id = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private SessionManager sessionManager = null;
    private CookieHandler cookieHandler = null;
    private StorageHandler storageHandler = null;
    private Map<String, Object> _localCache = null;

    protected Session() {}

    public Session(SessionManager sessionManager, String id, HttpServletRequest request, HttpServletResponse response) {
        this.id = id;
        this.request = request;
        this.response = response;
        this.sessionManager = sessionManager;
        this.cookieHandler = sessionManager.getCookieHandler();
        this.storageHandler = sessionManager.getStorageHandler();
    }

    /** 获取当前会话ID */
    public String getId() {
        return id;
    }
    
    /** 获取当前会话管理器 */
    public SessionManager getSessionManager() {
        return sessionManager;
    }
    
    /** 设置会话属性 */
    public void setAttribute(String name, Object value) throws SessionException {
        try {
            if (null == _localCache)  _localCache = new HashMap<String, Object>();
            _localCache.put(name, value);
            storageHandler.setAttribute(id, request, response, name, value);
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            throw new SessionException(e);
        }
    }

    /** 读取会话属性 */
    @SuppressWarnings("unchecked")
	public <V extends Object> V getAttribute(String name) throws SessionException {
    	Object value = null;
        try {
            if (null != _localCache) value = _localCache.get(name);
            if (null == value) value = storageHandler.getAttribute(id, request, response, name);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new SessionException(e);
        }
        return null != value ? (V) value : null;
    }
    
    /** 读取会话属性 */
	@SuppressWarnings("unchecked")
	public <V extends Object> V getAttributeAndRemove(String name) throws SessionException {
		Object value = null;
		try {
			value = storageHandler.getAttributeAndRemove(id, request, response, name);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new SessionException(e);
		}
		return null != value ? (V) value : null;
	}

    /** 移除会话属性 */
    public void removeAttribute(String name) throws SessionException {
        try {
        	if (null != _localCache) _localCache.remove(name);
            storageHandler.removeAttribute(id, request, response, name);
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            throw new SessionException(e);
        }
    }

    /** 会话失效 */
    public void invalidate() throws SessionException {
        try {
        	cookieHandler.removeSessionId(request, response);
            storageHandler.invalidate(id, request, response);
            
            id = storageHandler.createSessionId(request, response);
            cookieHandler.setSessionId(id, request, response);

            request.removeAttribute(SessionManager.REQUEST_SESSION_ATTRIBUTE_NAME);
            if (null != _localCache) _localCache.clear();
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            throw new SessionException(e);
        }
    }

}
