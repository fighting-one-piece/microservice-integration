package org.cisiondata.modules.kyfw.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;

public class KyfwS02CaptchaImage extends KyfwHandler {
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/passport/captcha/captcha-image?login_site=E&module=login&rand=sjrand&0.6755646629172731";
		Map<String, Object> result = HttpClientUtils.sendGetThenRespAndHeaders(url);
		Map<String, String> headers = (Map<String, String>) result.get("headers");
		Map<String, String> cookies = HeaderUtils.extractCookies(headers, "_passport_session", "_passport_ct", "BIGipServerpassport");
		cookies.putAll(params);
		print(cookies);
		byte[] content = (byte[]) result.get("content");
		if (null != content) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File("F:\\a.jpg"));
				fos.write(content);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(null != fos) fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return cookies;
	}

}
