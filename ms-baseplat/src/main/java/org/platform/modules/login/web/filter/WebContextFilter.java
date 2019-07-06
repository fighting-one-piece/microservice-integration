package org.platform.modules.login.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.login.web.WebContext;
import org.platform.modules.login.web.session.Session;
import org.platform.modules.login.web.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class WebContextFilter implements Filter {
	
	public Logger LOG = LoggerFactory.getLogger(WebContextFilter.class);
	
	private ServletContext ctx = null;
	private SessionManager sessionManager = null;

	public void init(FilterConfig config) throws ServletException {
		ctx = config.getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(ctx);
		sessionManager = wac.getBean(SessionManager.class);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.addHeader("Access-Control-Max-Age", "1800");
		httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
		httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT");
		httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
		httpServletResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, "
				+ "Access-Control-Allow-Headers, Authorization, X-Requested-With, Data, accessToken");
		httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Type, "
				+ "Access-Control-Allow-Headers, Authorization, X-Requested-With, Data");
		Session session = sessionManager.getSession(httpServletRequest, httpServletResponse);
		WebContext instance = new WebContext(httpServletRequest, httpServletResponse, session, ctx);
		try {
			WebContext.set(instance);
			chain.doFilter(request, response);
		} finally {
			WebContext.set(null);
		}
	}
	
	public void destroy() {
	}

}
