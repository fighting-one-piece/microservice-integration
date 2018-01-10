package org.cisiondata.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;

public class KyfwS24ResultOrderForDcQueue extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/resultOrderForDcQueue";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		String repeatSubmitToken = params.remove("repeatSubmitToken");
		String orderSequenceNo = params.remove("orderSequenceNo");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("orderSequence_no", orderSequenceNo);
		rparams.put("_json_att", "");
		rparams.put("REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"submitStatus":true},"messages":[],"validateMessages":{}}
		return params;
	}
	
	
}
