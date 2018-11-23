package org.platform.modules.bootstrap.filter;

import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletRequestWrapper;  

public class XSSHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private HttpServletRequest argsRequest = null;

	public XSSHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		argsRequest = request;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] parameterValues = super.getParameterValues(name);
		if (parameterValues == null)
			parameterValues = new String[] {};
		for (int i = 0; i < parameterValues.length; i++) {
			String value = parameterValues[i];
			if (value != null) {
				parameterValues[i] = xssEncode(value);
			}
		}
		return parameterValues;
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取<br/>
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String name) {
		String value = super.getParameter(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br/>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br/>
	 * getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {
		String value = super.getHeader(xssEncode(name));
		if (value != null) {
			value = xssEncode(value);
		}
		return value;
	}

	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符
	 * 
	 * @param s
	 * @return
	 */
	private static String xssEncode(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '>':
				sb.append("＞");// 转义大于号
				break;
			case '<':
				sb.append("＜");// 转义小于号
				break;
			case '\'':
				sb.append("＇");// 转义单引号
				break;
			case '\"':
				sb.append("＂");// 转义双引号
				break;
			case '&':
				sb.append("＆");// 转义&
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 获取最原始的request
	 * 
	 * @return
	 */
	public HttpServletRequest getArgsRequest() {
		return argsRequest;
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @return
	 */
	public static HttpServletRequest getArgsRequest(HttpServletRequest req) {
		if (req instanceof XSSHttpServletRequestWrapper) {
			return ((XSSHttpServletRequestWrapper) req).getArgsRequest();
		}
		return req;
	}

}
