package org.cisiondata.modules.boot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.json.GsonUtils;

public class WebTest {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = "http://host-125:10020/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "financial");
		params.put("t", "logistics");
		params.put("hl", 1);
		params.put("q", "13612345678");
		String response1 = HttpUtils.sendGet(url, params);
		System.err.println(response1);
		Map<String, Object> subparams = new HashMap<String, Object>();
		subparams.put("mobilePhone", "13512345678");
		subparams.put("linkMobilePhone", "13512345678");
//		params.put("map", subparams);
		params.put("q", URLEncoder.encode(GsonUtils.fromMapToJson(subparams), "utf-8"));
		String response2 = HttpUtils.sendGet(url, params);
		System.err.println(response2);
	}
	
}
