package org.cisiondata.modules.kyfw.crawler;

import java.util.Calendar;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;

public class KyfwS01LoginInit extends KyfwHandler {
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/login/init";
		Map<String, Object> result = HttpClientUtils.sendGetThenRespAndHeaders(url);
		Map<String, String> headers = (Map<String, String>) result.get("headers");
		System.err.println(headers);
		Map<String, String> cookies = HeaderUtils.extractCookies(headers, "JSESSIONID", "BIGipServerotn", "route");
		cookies.put("RAIL_DEVICEID", "QxDDyfGO24_jlbaCi01NUqYPW-ZgeiDlhEfJoXtds7URAu3jK8GTcUpw0vEiPmJ8VYhFfjK13qMSf_xRH3IEqyAL-7SVvdrcel0IOvMNkWcTye3zdKVTbThLe-vvMEuTZnVlJOCsN0-yVLL79oHIA4ZBPwFuItwj");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 12);
		cookies.put("RAIL_EXPIRATION", String.valueOf(calendar.getTimeInMillis()));
		cookies.put("_jc_save_wfdc_flag", "dc");
		return cookies;
	}
	
}
