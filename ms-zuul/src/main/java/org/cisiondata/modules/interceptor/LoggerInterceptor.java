package org.cisiondata.modules.interceptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cisiondata.utils.json.GsonUtils;
import org.cisiondata.utils.web.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoggerInterceptor implements HandlerInterceptor {
	
	private static Logger LOG = LoggerFactory.getLogger(LoggerInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		LogEntity logEntity = new LogEntity();
		logEntity.setSpendTime(System.currentTimeMillis());
		logEntity.setJsessionId(request.getRequestedSessionId());
		logEntity.setAccept(request.getHeader("accept"));
		logEntity.setUserAgent(request.getHeader("User-Agent"));
		logEntity.setIp(IPUtils.getIPAddress(request));
		logEntity.setUrl(request.getRequestURI());
		logEntity.setParams(getParams(request));
		logEntity.setHeaders(getHeaders(request));
		request.setAttribute("_log_entity", logEntity);
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
        LogEntity logEntity = (LogEntity) request.getAttribute("_log_entity");
        logEntity.setSpendTime((System.currentTimeMillis() - logEntity.getSpendTime()) / 1000);
        logEntity.setResult(response.toString());
        LOG.info("{}", logEntity.toString());
	}
	
	private String getParams(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        return GsonUtils.builder().toJson(params);
    }

	private String getHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> namesEnumeration = request.getHeaderNames();
        while(namesEnumeration.hasMoreElements()) {
            String name = namesEnumeration.nextElement();
            Enumeration<String> valueEnumeration = request.getHeaders(name);
            List<String> values = new ArrayList<>();
            while(valueEnumeration.hasMoreElements()) {
                values.add(valueEnumeration.nextElement());
            }
            headers.put(name, values);
        }
        return GsonUtils.builder().toJson(headers);
    }

}

class LogEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String jsessionId = null;
	
	private String accept = null;
	
	private String userAgent = null;
	
	private String ip = null;
	
	private String url = null;
	
	private String params = null;
	
	private String headers = null;
	
	private String result = null;
	
	private long spendTime = 0L;

	public String getJsessionId() {
		return jsessionId;
	}

	public void setJsessionId(String jsessionId) {
		this.jsessionId = jsessionId;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getSpendTime() {
		return spendTime;
	}

	public void setSpendTime(long spendTime) {
		this.spendTime = spendTime;
	}
	
	public String toBlock(Object msg) {
		return "$" + (null == msg ? "" : String.valueOf(msg)) + "$";
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toBlock(this.jsessionId));
		sb.append(toBlock(this.accept));
		sb.append(toBlock(this.userAgent));
		sb.append(toBlock(this.headers));
		sb.append(toBlock(this.ip));
		sb.append(toBlock(this.url));
		sb.append(toBlock(this.params));
		sb.append(toBlock(this.result));
		sb.append(toBlock(this.spendTime));
		return sb.toString();
	}
}
