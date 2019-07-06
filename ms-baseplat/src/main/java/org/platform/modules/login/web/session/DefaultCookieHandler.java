package org.platform.modules.login.web.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.login.Constants;
import org.springframework.stereotype.Component;

@Component("defaultCookieHandler")
public class DefaultCookieHandler implements CookieHandler {

	private String cookieName = Constants.COOKIE_USER_SESSION;
	private String domain;
	private String path = "/";
	private int cookieMaxAge = -1;
	private boolean secure = true;

	public DefaultCookieHandler() {
	}

	@Override
	public void setSessionId(String sessionId, HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, sessionId);
		if (domain != null && !domain.isEmpty()) {
			cookie.setDomain(domain);
		}
		if (path != null && !path.isEmpty()) {
			cookie.setPath(path);
		} else {
			String contextPath = request.getContextPath();
			if (contextPath == null) {
				cookie.setPath("/");
			} else {
				cookie.setPath(contextPath);
			}
		}
		if (cookieMaxAge > 0) {
			cookie.setMaxAge(cookieMaxAge);
		} else {
			cookie.setMaxAge(-1);
		}
		cookie.setSecure(secure);
		response.addCookie(cookie);
	}

	@Override
	public void removeSessionId(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(cookieName, "");
		if (domain != null && !domain.isEmpty()) {
			cookie.setDomain(domain);
		}
		if (path != null && !path.isEmpty()) {
			cookie.setPath(path);
		}
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	@Override
	public String getSessionId(HttpServletRequest request, HttpServletResponse response) {
		String sessionId = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					sessionId = cookie.getValue();
					break;
				}
			}
		}
		return sessionId;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getCookieDomain() {
		return domain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.domain = cookieDomain;
	}

	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	public String getCookiePath() {
		return path;
	}

	public void setCookiePath(String cookiePath) {
		this.path = cookiePath;
	}

	public boolean isCookieSecure() {
		return secure;
	}

	public void setCookieSecure(boolean cookieSecure) {
		this.secure = cookieSecure;
	}
	
}
