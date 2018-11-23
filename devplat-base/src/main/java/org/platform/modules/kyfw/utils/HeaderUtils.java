package org.platform.modules.kyfw.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeaderUtils {
	
	public static String[] buildHeaders(Map<String, String> headers) {
		return new String[]{
				"Accept", "application/json, text/javascript, */*; q=0.01",
				"Accept-Encoding", "gzip, deflate, br",
				"Accept-Language", "zh-CN,zh;q=0.8",
				"Connection", "keep-alive",
				"Content-Type", "application/x-www-form-urlencoded; charset=UTF-8",
				"Cookie", headers.get("Cookie"),
				"Host", "kyfw.12306.cn",
				"Origin", "https://kyfw.12306.cn",
				"Referer", headers.get("Referer"),
				"X-Requested-With", "XMLHttpRequest",
				"User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.91 Safari/537.36"
			};
	}
	
	/**
	 * 从Header中抽取Cookies
	 * @param headers
	 * @return
	 */
	public static Map<String, String> extractCookies(Map<String, String> headers) {
		Map<String, String> cookies = new HashMap<String, String>();
		if (headers.containsKey("Set-Cookie")) {
			String[] setCookie = headers.get("Set-Cookie").split(";");
			if (null != setCookie) {
				for (int i = 0, len = setCookie.length; i < len; i++) {
					String[] cookie = setCookie[i].split("=");
					cookies.put(cookie[0].trim(), cookie[1].trim());
				}
			}
		}
		return cookies;
	}
	
	/**
	 * 从Header中抽取指定Cookies
	 * @param headers
	 * @param includeCookies
	 * @return
	 */
	public static Map<String, String> extractCookies(Map<String, String> headers, String... includeCookies) {
		Set<String> targetCookies = new HashSet<String>();
		if (null != includeCookies) {
			for (int i = 0, len = includeCookies.length; i < len; i++) {
				targetCookies.add(includeCookies[i]);
			}
		}
		Map<String, String> cookies = new HashMap<String, String>();
		if (headers.containsKey("Set-Cookie")) {
			String[] setCookie = headers.get("Set-Cookie").split(";");
			if (null != setCookie) {
				for (int i = 0, len = setCookie.length; i < len; i++) {
					String[] cookie = setCookie[i].split("=");
					String cookieKey = cookie[0].trim(); 
					if (targetCookies.contains(cookieKey)) {
						cookies.put(cookieKey, cookie[1].trim());
					}
				}
			}
		}
		return cookies;
	}
	
}
