package org.cisiondata.modules.kyfw.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cisiondata.utils.date.DateFormatter;
import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainNumberService {
	
	public static Logger LOG = LoggerFactory.getLogger(TrainNumberService.class);
	
	private static String TRAIN_NUMBER_URL = "https://train.qunar.com/dict/open/s2s.do?callback=&dptStation=%s&arrStation=%s&date=%s&type=normal&user=neibu&source=site&start=1&num=500&sort=3&_=%s";
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> obtainTrainNumberInfos(String startPlace, String endPlace) {
		String date = DateFormatter.DATE.get().format(new Date());
		String url = String.format(TRAIN_NUMBER_URL, startPlace, endPlace, date, System.currentTimeMillis());
		String response = HttpUtils.sendGet(url);
		response = response.substring(response.indexOf("(") + 1, response.length() - 2);
		Map<String, Object> map = GsonUtils.fromJsonToMap(response);
		Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
		List<Map<String, Object>> s2sBeanList = (List<Map<String,Object>>) dataMap.get("s2sBeanList");
		List<Map<String, Object>> trainNumberInfos = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> s2sBean : s2sBeanList) {
			Object trainNo = s2sBean.get("trainNo").toString().replace("\"", "");
			Object dptStationName = s2sBean.get("dptStationName").toString().replace("\"", "");
			Object endStationName = s2sBean.get("endStationName").toString().replace("\"", "");
			Object dptTime = s2sBean.get("dptTime").toString().replace("\"", "");
			Object startDate = s2sBean.get("startDate").toString();		
			Map<String, Map<String, Object>> seats = (Map<String, Map<String, Object>>) s2sBean.get("seats");
			for (Map.Entry<String, Map<String, Object>> entry : seats.entrySet()) {
				Map<String, Object> seatInfo = entry.getValue();
				Map<String,Object> trainNumberInfo = new HashMap<>();
				trainNumberInfo.put("trainNo", trainNo);
				trainNumberInfo.put("dptStationName", dptStationName);
				trainNumberInfo.put("endStationName", endStationName);
				trainNumberInfo.put("dptTime", dptTime);
				trainNumberInfo.put("startDate", startDate);
				trainNumberInfo.put("seatType", entry.getKey());
				int seatCount = Integer.parseInt(String.valueOf(seatInfo.get("count")));
				if (seatCount == 0) continue;
				trainNumberInfo.put("seatCount", seatCount);
				System.err.println(trainNumberInfo);
				trainNumberInfos.add(trainNumberInfo);
			}
		}
		return trainNumberInfos;
	}
	
}
