package org.cisiondata.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;

public class KyfwS18GetQueueCount extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		String passengerTicketStr = params.remove("passengerTicketStr");
		String oldPassengerStr = params.remove("oldPassengerStr");
		String repeatSubmitToken = params.remove("repeatSubmitToken");
		String leftTicketStr = params.remove("leftTicketStr");
		String keyCheckIsChange = params.remove("keyCheckIsChange");
		String trainNo = params.remove("trainNo");
		String trainDate = params.remove("trainDate");
		String trainLocation = params.remove("trainLocation");
		String stationTrainCode = params.remove("stationTrainCode");
		String fromStationTelecode = params.remove("fromStationTelecode");
		String toStationTelecode = params.remove("toStationTelecode");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		/**
		Calendar calendar = Calendar.getInstance();  
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT+0800 (中国标准时间)'", Locale.US);  
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    String trainDate = sdf.format(calendar.getTime());  
		*/
		rparams.put("train_date", trainDate);
		rparams.put("train_no", trainNo);
		rparams.put("stationTrainCode", stationTrainCode);
		rparams.put("seatType", "3");
		rparams.put("fromStationTelecode", fromStationTelecode);
		rparams.put("toStationTelecode", toStationTelecode);
		rparams.put("leftTicket", leftTicketStr);
		rparams.put("purpose_codes", "00");
		rparams.put("train_location", trainLocation);
		rparams.put("_json_att", "");
		rparams.put("REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"count":"2","ticket":"42","op_2":"false","countT":"0","op_1":"true"},"messages":[],"validateMessages":{}}
		params.put("passengerTicketStr", passengerTicketStr);
		params.put("oldPassengerStr", oldPassengerStr);
		params.put("repeatSubmitToken", repeatSubmitToken);
		params.put("leftTicketStr", leftTicketStr);
		params.put("keyCheckIsChange", keyCheckIsChange);
		params.put("trainLocation", trainLocation);
		return params;
	}
	
}
