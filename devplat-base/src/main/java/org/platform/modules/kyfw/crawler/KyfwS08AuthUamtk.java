package org.platform.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;
import org.platform.utils.json.GsonUtils;

public class KyfwS08AuthUamtk extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/passport/web/auth/uamtk";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("appid", "otn");
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"result_message":"验证通过","result_code":0,"apptk":null,"newapptk":"qN0XQaWWJ_F6vRC4whS-5pV7ih1oP9X4uqSQrisVR1A518180"}
		Map<String, Object> result = GsonUtils.fromJsonToMap(response);
		params.put("tk", String.valueOf(result.get("newapptk")));
		params.remove("uamtk");
		params.remove("_passport_ct");
		params.remove("_passport_session");
		return params;
	}
	
}
