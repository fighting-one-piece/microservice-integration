package org.platform.utils.pub;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

public class URLEncodeUtils {

	/**
	 * 将字符串进行URL编码
	 * @param url
	 * @return
	 */
	public static String EncodeURL(String url) {
		if (StringUtils.isBlank(url)) return null;
		try {
			return URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String DecodeURL(String url) {
		if (StringUtils.isBlank(url)) return null;
		try {
			return URLDecoder.decode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
