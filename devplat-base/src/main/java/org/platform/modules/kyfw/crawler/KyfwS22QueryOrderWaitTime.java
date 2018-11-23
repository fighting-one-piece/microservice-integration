package org.platform.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;
import org.platform.utils.json.GsonUtils;

public class KyfwS22QueryOrderWaitTime extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		//String url = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime";
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?random=1513821816179&tourFlag=dc&_json_att=&REPEAT_SUBMIT_TOKEN=$REPEAT_SUBMIT_TOKEN";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		String repeatSubmitToken = params.remove("repeatSubmitToken");
		headers.put("Cookie", buildCookie(params));
		url = url.replace("$REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		/**
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("random", "1513821813165");
		rparams.put("tourFlag", "dc");
		rparams.put("_json_att", "");
		rparams.put("REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		*/
		String orderSequenceNo = null;
		while (null == orderSequenceNo) {
			String response = HttpClientUtils.sendGet(url, HeaderUtils.buildHeaders(headers));
			System.err.println(response);
			if (response.startsWith("{") && response.endsWith("}")) {
				Map<String, Object> result = GsonUtils.fromJsonToMap(response);
				Object data = result.get("data");
				if (null != data) {
					Map<String, Object> dresult = GsonUtils.fromJsonToMap(String.valueOf(data));
					if (dresult.containsKey("orderId")) {
						orderSequenceNo = String.valueOf(dresult.get("orderId"));
					}
				}
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/**
		parse orderId
		{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":4,"requestId":6349428899208270301,"waitCount":1,"tourFlag":"dc","orderId":null},"messages":[],"validateMessages":{}}
		{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"queryOrderWaitTimeStatus":true,"count":0,"waitTime":-1,"requestId":6349428899208270301,"waitCount":0,"tourFlag":"dc","orderId":"E720919243"},"messages":[],"validateMessages":{}}
		*/
		params.put("orderSequenceNo", orderSequenceNo);
		params.put("repeatSubmitToken", repeatSubmitToken);
		return params;
	}
	
}
