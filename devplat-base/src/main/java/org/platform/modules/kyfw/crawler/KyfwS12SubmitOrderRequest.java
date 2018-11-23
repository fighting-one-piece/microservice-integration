package org.platform.modules.kyfw.crawler;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;
import org.platform.utils.json.GsonUtils;

public class KyfwS12SubmitOrderRequest extends KyfwHandler {
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
		String secretStr = params.remove("secretStr");
		String trainDate = params.remove("trainDate");
		String fromStationName = params.remove("fromStationName");
		String toStationName = params.remove("toStationName");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		try {
			System.err.println("secretStr: " + secretStr);
			rparams.put("secretStr", URLDecoder.decode(secretStr, "UTF-8"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		rparams.put("train_date", trainDate);
		rparams.put("back_train_date", trainDate);
		rparams.put("tour_flag", "dc");
		rparams.put("purpose_codes", "ADULT");
		rparams.put("query_from_station_name", fromStationName);
		rparams.put("query_to_station_name", toStationName);
		Map<String, Object> result = HttpClientUtils.sendPostThenRespAndHeaders(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		String content = String.valueOf(result.get("content"));
		System.err.println(content);
		if (content.startsWith("{") && content.endsWith("}")) {
			Map<String, Object> cresult = GsonUtils.fromJsonToMap(content);
			String data = String.valueOf(cresult.get("data"));
			if ("N".equalsIgnoreCase(data)) {
				Map<String, String> rheaders = (Map<String, String>) result.get("headers");
				System.err.println(rheaders);
				Map<String, String> cookies = HeaderUtils.extractCookies(rheaders, "tk");
				if (null != cookies && cookies.containsKey("tk")) {
					System.err.println("tk: " + cookies.get("tk"));
					params.put("tk", cookies.get("tk"));
				}
			}
		}
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":"Y","messages":[],"validateMessages":{}}
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":"N","messages":[],"validateMessages":{}}
		return params;
	}
	
	public static void main(String[] args) throws Exception {
		System.err.println(URLDecoder.decode("%22", "UTF-8"));
	}
	
}
