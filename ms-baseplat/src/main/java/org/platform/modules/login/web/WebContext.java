package org.platform.modules.login.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.login.web.session.Session;

public class WebContext {
	
	private static ThreadLocal<WebContext> INSTANCE = new ThreadLocal<WebContext>();
	private Session session = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private ServletContext servletContext = null;

	public WebContext(HttpServletRequest request, HttpServletResponse response, 
			Session session, ServletContext servletContext) {
		this.session = session;
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;
	}

	public static WebContext get() {
		return WebContext.INSTANCE.get();
	}

	public static void set(WebContext context) {
		WebContext.INSTANCE.set(context);
	}

	public Session getSession() {
		return session;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}
	
}
