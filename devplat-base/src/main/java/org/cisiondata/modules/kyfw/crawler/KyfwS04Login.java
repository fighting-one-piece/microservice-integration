package org.cisiondata.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;
import org.cisiondata.utils.json.GsonUtils;

public class KyfwS04Login extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/passport/web/login";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/login/init");
		/**
		params.remove("JSESSIONID");
		*/
		params.put("_jc_save_fromStation", "%u6210%u90FD%2CCDW");
		params.put("_jc_save_toStation", "%u5317%u4EAC%2CBJP");
		params.put("_jc_save_fromDate", "2017-12-21");
		params.put("_jc_save_toDate", "2017-12-21");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("username", "");
		rparams.put("password", "");
		rparams.put("appid", "otn");
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"result_message":"登录成功","result_code":0,"uamtk":"-VlAN5v57eT--SjPuyZ4lYcJURK69Dws61eEMBZFVKsbc8180"}
		Map<String, Object> result = GsonUtils.fromJsonToMap(response);
		params.put("uamtk", String.valueOf(result.get("uamtk")));
		return params;
	}

}
