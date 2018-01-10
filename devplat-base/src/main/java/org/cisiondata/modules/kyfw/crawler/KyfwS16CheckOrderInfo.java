package org.cisiondata.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;

public class KyfwS16CheckOrderInfo extends KyfwHandler {
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		String repeatSubmitToken = params.remove("repeatSubmitToken");
		String leftTicketStr = params.remove("leftTicketStr");
		String keyCheckIsChange = params.remove("keyCheckIsChange");
		String trainNo = params.remove("trainNo");
		String trainDate = params.remove("trainDate");
		String trainLocation = params.remove("trainLocation");
		String stationTrainCode = params.remove("stationTrainCode");
		String fromStationTelecode = params.remove("fromStationTelecode");
		String toStationTelecode = params.remove("toStationTelecode");
		String passenger = params.remove("passenger");
		String passengerAndMobile = params.remove("passengerAndMobile");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("cancel_flag", "2");
		rparams.put("bed_level_order_num", "000000000000000000000000000000");
		//3,0,1,XX,1,653101XXXXXXXXXXXX,159XXXXXXXX,N
		rparams.put("passengerTicketStr", "3,0,1," + passengerAndMobile + ",N");
		//XX,1,653101XXXXXXXXXXXX,1_
		rparams.put("oldPassengerStr", passenger + ",1_");
		rparams.put("tour_flag", "dc");
		rparams.put("randCode", "");
		rparams.put("whatsSelect", "1");
		rparams.put("_json_att", "");
		rparams.put("REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		Map<String, Object> result = HttpClientUtils.sendPostWithHeaders(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(result.get("content"));
		Map<String, String> cookies = HeaderUtils.extractCookies((Map<String, String>) result.get("headers"), "tk");
		if (null != cookies && cookies.containsKey("tk")) {
			params.put("tk", cookies.get("tk"));
		}
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"ifShowPassCode":"N","canChooseBeds":"N","canChooseSeats":"N","choose_Seats":"MOP9","isCanChooseMid":"N","ifShowPassCodeTime":"1","submitStatus":true,"smokeStr":""},"messages":[],"validateMessages":{}}
		params.put("passengerTicketStr", rparams.get("passengerTicketStr"));
		params.put("oldPassengerStr", rparams.get("oldPassengerStr"));
		params.put("repeatSubmitToken", repeatSubmitToken);
		params.put("leftTicketStr", leftTicketStr);
		params.put("keyCheckIsChange", keyCheckIsChange);
		params.put("trainNo", trainNo);
		params.put("trainDate", trainDate);
		params.put("trainLocation", trainLocation);
		params.put("stationTrainCode", stationTrainCode);
		params.put("fromStationTelecode", fromStationTelecode);
		params.put("toStationTelecode", toStationTelecode);
		return params;
	}

}
