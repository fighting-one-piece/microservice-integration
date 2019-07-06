package org.platform.modules.login.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.utils.serde.SerializerUtils;

/** 会话存储器适配器 */
public class StorageHandlerAdapter implements StorageHandler {

	private static final long serialVersionUID = 1L;
	
	private static final String keyPrefix = "bp";
	
	protected static final int valueTTL = 24 * 3600;

	protected String sessionKey(String sessionId) {
		return keyPrefix + ":session:id:" + sessionId;
	}

	protected byte[] hashKey(String sessionId) {
		return SerializerUtils.write(keyPrefix + ":session:" + sessionId);
	}

	protected byte[] flagHashKey(String sessionId, String name) {
		return SerializerUtils.write(keyPrefix + ":session:" + sessionId + ":" + name);
	}

	@Override
	public String createSessionId() throws SessionException {
		return null;
	}

	@Override
	public String createSessionId(HttpServletRequest request, HttpServletResponse response) throws SessionException {
		return createSessionId();
	}

	@Override
	public boolean existsSessionId(String sessionId) throws SessionException {
		return false;
	}

	@Override
	public void initialize(String sessionId) throws SessionException {
	}

	@Override
	public void invalidate(String sessionId) throws SessionException {
	}

	@Override
	public void invalidate(String sessionId, HttpServletRequest request, HttpServletResponse response) throws SessionException {
		initialize(sessionId);
	}

	@Override
	public void setAttribute(String sessionId, String name, Object value) throws SessionException {
	}

	@Override
	public void setAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name, Object value) throws SessionException {
		setAttribute(sessionId, name, value);
	}

	@Override
	public Object getAttribute(String sessionId, String name) throws SessionException {
		return null;
	}

	@Override
	public Object getAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name) throws SessionException {
		return getAttribute(sessionId, name);
	}

	@Override
	public Object getAttributeAndRemove(String sessionId, String name) throws SessionException {
		return null;
	}

	@Override
	public Object getAttributeAndRemove(String sessionId, HttpServletRequest request, HttpServletResponse response,
			String name) throws SessionException {
		return getAttributeAndRemove(sessionId, name);
	}

	@Override
	public void removeAttribute(String sessionId, String name) throws SessionException {
	}

	@Override
	public void removeAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name) throws SessionException {
		removeAttribute(sessionId, name);
	}
	
}
