package org.platform.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;

public class KyfwS05PassportRedirect extends KyfwHandler {
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/login/init");
		headers.put("Cookie", buildCookie(params));
		Map<String, Object> result = HttpClientUtils.sendGetThenRespAndHeaders(url, HeaderUtils.buildHeaders(headers));
		Map<String, String> rheaders = (Map<String, String>) result.get("headers");
		Map<String, String> cookies = HeaderUtils.extractCookies(rheaders, "JSESSIONID");
		System.err.println("JSESSIONID: " + cookies.get("JSESSIONID"));
		params.putAll(cookies);
		return params;
	}

}
