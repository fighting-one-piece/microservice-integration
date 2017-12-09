package org.cisiondata.modules.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggerInterceptor implements HandlerInterceptor {
	
	private static Logger LOG = LoggerFactory.getLogger(LoggerInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
        request.setAttribute("_start_time", System.currentTimeMillis());
        request.setAttribute("_logger_object", null);
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
        //请求开始时间
        long startTime = Long.valueOf(request.getAttribute("_start_time").toString());
        Object loggerObject = request.getAttribute("_logger_object");
        LOG.info("{} {}", startTime, loggerObject);
	}

}
