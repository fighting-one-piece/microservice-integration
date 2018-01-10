package org.cisiondata.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cisiondata.modules.kyfw.utils.HeaderUtils;
import org.cisiondata.utils.http.HttpClientUtils;
import org.cisiondata.utils.json.GsonUtils;

public class KyfwS15GetPassengers extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/confirmPassenger/initDc");
		String repeatSubmitToken = params.remove("globalRepeatSubmitToken");
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
		rparams.put("_json_att", "");
		rparams.put("REPEAT_SUBMIT_TOKEN", repeatSubmitToken);
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		if (response.startsWith("{") && response.endsWith("}")) {
			Map<String, Object> result = GsonUtils.fromJsonToMap(response);
			Object data = result.get("data");
			if (null != data) {
				Map<String, Object> dresult = GsonUtils.fromJsonToMap(String.valueOf(data));
				Object normal_passengers = dresult.get("normal_passengers");
				if (null != normal_passengers) {
					List<Map<String, Object>> list = GsonUtils.fromJsonToList(String.valueOf(normal_passengers));
					Map<String, Object> passenger = list.get(0);
					String passengerInfo = passenger.get("passenger_name") + ",1," + passenger.get("passenger_id_no");
					params.put("passenger", passengerInfo);
					params.put("passengerAndMobile", passengerInfo + "," + passenger.get("mobile_no"));
				}
			}
		}
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
