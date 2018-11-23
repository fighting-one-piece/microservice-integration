package org.platform.modules.kyfw.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;

public class KyfwS03CaptchaCheck extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/passport/captcha/captcha-check";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/login/init");
		params.put("current_captcha_type", "Z");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		System.out.print("enter anster: ");  
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			String line = br.readLine();
			rparams.put("answer", URLEncoder.encode(judgeInput(line), "UTF-8"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			if (null != br)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		rparams.put("login_site", "E");
		rparams.put("rand", "sjrand");
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"result_message":"验证码校验成功","result_code":"4"}
		//params.remove("current_captcha_type");
		return params;
	}
	
	public static final String img11 = "35,59";
	public static final String img12 = "111,59";
	public static final String img13 = "182,59";
	public static final String img14 = "256,59";
	public static final String img21 = "35,134";
	public static final String img22 = "111,134";
	public static final String img23 = "182,134";
	public static final String img24 = "256,134";
	
	private static Map<String, String> imgs = new HashMap<String, String>();
	
	static {
		imgs.put("1", img11);
		imgs.put("2", img12);
		imgs.put("3", img13);
		imgs.put("4", img14);
		imgs.put("5", img21);
		imgs.put("6", img22);
		imgs.put("7", img23);
		imgs.put("8", img24);
	}
	
	public String judgeInput(String input) {
		if (null == input || "".equals(input)) throw new RuntimeException("input is null");
		String[] results = input.contains(",") ? input.split(",") : new String[]{input};
		StringBuilder sb = new StringBuilder(50);
		for (int i = 0, len = results.length; i < len; i++) {
			sb.append(imgs.get(results[i])).append(",");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
}
