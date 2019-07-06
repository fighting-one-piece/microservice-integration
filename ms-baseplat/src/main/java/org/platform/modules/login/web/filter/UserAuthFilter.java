package org.platform.modules.login.web.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.platform.modules.login.Constants;
import org.platform.modules.login.service.IUserAuthService;
import org.platform.modules.login.web.session.SessionManager;
import org.platform.modules.user.entity.UserPO;
import org.platform.utils.cache.CacheKey;
import org.platform.utils.cache.CacheUtils;
import org.platform.utils.date.DateFormatter;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.token.TokenUtils;
import org.platform.utils.web.IPUtils;
import org.platform.utils.web.URLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 只针对用户的认证、授权 */
public class UserAuthFilter implements Filter {

	private Logger LOG = LoggerFactory.getLogger(UserAuthFilter.class);
	
	private SimpleDateFormat DF = DateFormatter.DATE.get();

	private ObjectMapper objectMapper = new ObjectMapper();
	
	private SessionManager sessionManager = null;
	
	private IUserAuthService userAuthService = null;

	private Set<String> notAuthenticationUrls = null;

	public void init(FilterConfig config) throws ServletException {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		sessionManager = wac.getBean(SessionManager.class);
		userAuthService = wac.getBean(IUserAuthService.class);
		notAuthenticationUrls = URLFilter.notAuthenticationUrls();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String requestUrl = httpServletRequest.getServletPath();
		LOG.info("client current request url: {}", requestUrl);
		if (requestUrl.startsWith("/api")) requestUrl = requestUrl.replaceAll("/api/v.", "");
		if (!notAuthenticationUrls.contains(requestUrl)) {
			try {
				if (!authenticationRequest(requestUrl, httpServletRequest, httpServletResponse)) {
					writeResponse(httpServletResponse, wrapperFailureWebResult(ResultCode.VERIFICATION_USER_FAIL));
					return;
				}
			} catch (BusinessException be) {
				writeResponse(httpServletResponse, wrapperFailureWebResult(be.getCode(), be.getMessage()));
				return;
			}
			if (!userAuthService.readResourceAuthorization(requestUrl)) {
				writeResponse(httpServletResponse, wrapperFailureWebResult(ResultCode.RESOURCE_NOT_ACCESS));
				return;
			}
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
	}

	private boolean authenticationRequest(String requestUrl, HttpServletRequest request, HttpServletResponse response) throws BusinessException {
		String accessToken = request.getHeader("accessToken");
		LOG.info("request url {} access token {}", requestUrl, accessToken);
		try {
			Object sessionIdObj = RedisClusterUtils.getInstance().get(accessToken);
			if (null == sessionIdObj) return false;
			String sessionId = (String) sessionIdObj;
			Object userPOObject = sessionManager.getStorageHandler().getAttribute(sessionId, 
					request, response, Constants.SESSION_CURRENT_USER);
			if (null == userPOObject) return false;
			UserPO userPO = (UserPO) userPOObject;
			int authenticationNumber = Integer.parseInt(accessToken.substring(0, 1));
			LOG.info("authentication number {}", authenticationNumber);
			String authenticationToken = TokenUtils.genNAuthenticationMD5Token(authenticationNumber, 
				userPO.getAccount(), userPO.getPassword(), IPUtils.getIPAddress(request), DF.format(new Date()));
			if (!accessToken.startsWith(authenticationToken)) return false;
			String idCacheKey = CacheUtils.genCacheKey(CacheKey.USER.ID, userPO.getId());
			String usessionId = (String) RedisClusterUtils.getInstance().hget(idCacheKey, "sessionId");
			if (!sessionId.equals(usessionId)) {
				throw new BusinessException(ResultCode.VERIFICATION_USER_SESSION_FAIL);
			}
			RedisClusterUtils.getInstance().expire(idCacheKey, 1800);
			RedisClusterUtils.getInstance().expire(accessToken, 1800);
			return true;
		} catch (BusinessException be) {
			LOG.error(be.getMessage(), be);
			throw be;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
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
