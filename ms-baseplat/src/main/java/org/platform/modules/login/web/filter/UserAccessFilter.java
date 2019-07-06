package org.platform.modules.login.web.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.web.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 只针对用户访问 */
public class UserAccessFilter implements Filter {

	public Logger LOG = LoggerFactory.getLogger(UserAccessFilter.class);
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private Set<String> notAccessCountUrls = new HashSet<String>();

	public void init(FilterConfig config) throws ServletException {
		notAccessCountUrls.add("/favicon.ico");
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String requestUrl = httpServletRequest.getServletPath();
		if (!notAccessCountUrls.contains(requestUrl)) {
			String ipAddress = IPUtils.getIPAddress(httpServletRequest);
			long accessCount = RedisClusterUtils.getInstance().incr(ipAddress);
			if (accessCount == 1) RedisClusterUtils.getInstance().expire(ipAddress, 3600);
			LOG.info("ip {} access count {}", ipAddress, accessCount);
			if (accessCount > 50000) {
				writeResponse(httpServletResponse, wrapperFailureWebResult(ResultCode.IP_NOT_ACCESS));
				return;
			}
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

	private void writeResponse(HttpServletResponse response, Object result)
			throws JsonGenerationException, JsonMappingException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		response.getWriter().write(objectMapper.writeValueAsString(result));
	}

	private WebResult wrapperFailureWebResult(int code, String failure) {
		return new WebResult().buildFailure(code, failure);
	}
	
	private WebResult wrapperFailureWebResult(ResultCode resultCode) {
		return wrapperFailureWebResult(resultCode.getCode(), resultCode.getDesc());
	}

}
