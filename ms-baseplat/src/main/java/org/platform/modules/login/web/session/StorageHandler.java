package org.platform.modules.login.web.session;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 会话存储器, 负责存储会话数据 */
public interface StorageHandler extends Serializable {
	
	/**
	 * 创建会话ID
	 * @return
	 * @throws SessionException
	 */
	public String createSessionId() throws SessionException;
	
	/**
	 * 创建会话ID
	 * @param request
	 * @param response
	 * @return
	 * @throws SessionException
	 */
	public String createSessionId(HttpServletRequest request, HttpServletResponse response) throws SessionException;
	
	/**
	 * 会话ID是否存在
	 * @param sessionId
	 * @return
	 * @throws SessionException
	 */
	public boolean existsSessionId(String sessionId) throws SessionException;

	/**
	 * 初始化会话
	 * @param sessionId
	 * @throws SessionException
	 */
	public void initialize(String sessionId) throws SessionException;
	
	/**
	 * 会话失效
	 * @param sessionId
	 * @throws SessionException
	 */
	public void invalidate(String sessionId) throws SessionException;

	/**
	 * 会话失效
	 * @param sessionId
	 * @param request
	 * @param response
	 * @throws SessionException
	 */
	public void invalidate(String sessionId, HttpServletRequest request, HttpServletResponse response) throws SessionException;

	/**
	 * 设置会话属性
	 * @param sessionId
	 * @param name
	 * @param value
	 * @throws SessionException
	 */
	public void setAttribute(String sessionId, String name, Object value) throws SessionException;
	
	/**
	 * 设置会话属性
	 * @param sessionId
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @throws SessionException
	 */
	public void setAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name, Object value) throws SessionException;

	/**
	 * 获取会话属性
	 * @param sessionId
	 * @param name
	 * @return 
	 * @throws SessionException
	 */
	public Object getAttribute(String sessionId, String name) throws SessionException;
	
	/**
	 * 获取会话属性
	 * @param sessionId
	 * @param request
	 * @param response
	 * @param name
	 * @return 
	 * @throws SessionException
	 */
	public Object getAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name) throws SessionException;
	
	/**
	 * 获取会话属性并移除
	 * @param sessionId
	 * @param name
	 * @return
	 * @throws SessionException - 如果发生会话异常
	 */
	public Object getAttributeAndRemove(String sessionId, String name) throws SessionException;
	
	/**
	 * 获取会话属性并移除
	 * @param sessionId
	 * @param request
	 * @param response
	 * @param name
	 * @return
	 * @throws SessionException - 如果发生会话异常
	 */
	public Object getAttributeAndRemove(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name) throws SessionException;

	/**
	 * 删除会话属性
	 * @param sessionId
	 * @param name
	 * @throws SessionException
	 */
	public void removeAttribute(String sessionId, String name) throws SessionException;
	
	/**
	 * 删除会话属性
	 * @param sessionId
	 * @param request
	 * @param response
	 * @param name
	 * @throws SessionException
	 */
	public void removeAttribute(String sessionId, HttpServletRequest request, HttpServletResponse response, 
			String name) throws SessionException;
	
}
