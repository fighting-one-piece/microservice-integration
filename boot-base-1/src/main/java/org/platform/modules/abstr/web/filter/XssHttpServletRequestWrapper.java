package org.platform.modules.abstr.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.util.HtmlUtils;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public XssHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public String getHeader(String name) {
		String value = super.getHeader(name);
		return HtmlUtils.htmlEscape(value);
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		return HtmlUtils.htmlEscape(value);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values != null) {
			int length = values.length;
			String[] escapseValues = new String[length];
			for (int i = 0; i < length; i++) {
				escapseValues[i] = HtmlUtils.htmlEscape(values[i]);
			}
			return escapseValues;
		}
		return super.getParameterValues(name);
	}

}
