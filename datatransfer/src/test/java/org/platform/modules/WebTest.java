package org.platform.modules;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.platform.utils.http.HttpUtils;
import org.platform.utils.json.GsonUtils;

public class WebTest {
	
	@Test
	public void t1() throws UnsupportedEncodingException {
		String url = "http://host-125:10020/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "financial");
		params.put("t", "logistics");
		params.put("hl", 1);
		params.put("q", URLEncoder.encode("13612345678", "utf-8"));
		params.put("pn", 1);
		params.put("rn", 2);
		String response1 = HttpUtils.sendGet(url, params);
		System.err.println(response1);
		Map<String, Object> subparams = new HashMap<String, Object>();
		subparams.put("mobilePhone", "13512345678");
		subparams.put("linkMobilePhone", "13512345678");
		params.put("q", URLEncoder.encode(GsonUtils.fromMapToJson(subparams), "utf-8"));
		String response2 = HttpUtils.sendGet(url, params);
		System.err.println(response2);
	}
	
	public static void main(String[] args) {
	}
	
}
