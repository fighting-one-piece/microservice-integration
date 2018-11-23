package org.platform.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;

public class KyfwS20ConfirmSingleForQueue extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		String passengerTicketStr = params.remove("passengerTicketStr");
		String oldPassengerStr = params.remove("oldPassengerStr");
		String repeatSubmitToken = params.remove("repeatSubmitToken");
		String leftTicketStr = params.remove("leftTicketStr");
		String keyCheckIsChange = params.remove("keyCheckIsChange");
		String trainLocation = params.remove("trainLocation");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("passengerTicketStr", passengerTicketStr);
		rparams.put("oldPassengerStr", oldPassengerStr);
		rparams.put("randCode", "");
		rparams.put("purpose_codes", "00");
		rparams.put("key_check_isChange", keyCheckIsChange);
		rparams.put("leftTicketStr", leftTicketStr);
		rparams.put("train_location", trainLocation);
		rparams.put("choose_seats", "");
		rparams.put("seatDetailType", "000");
		rparams.put("whatsSelect", "1");
		rparams.put("roomType", "00");
		rparams.put("dwAll", "N");
		rparams.put("_json_att", "");
		rparams.put("REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"count":"2","ticket":"42","op_2":"false","countT":"0","op_1":"true"},"messages":[],"validateMessages":{}}
		params.put("repeatSubmitToken", repeatSubmitToken);
		return params;
	}
	
	public static void main(String[] args) {
		
	}
	
}
