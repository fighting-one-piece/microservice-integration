package org.platform.modules.kyfw.crawler;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;

public class KyfwS14ConfirmPassengerInitDc extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
		headers.put("Cookie", buildCookie(params));
		Map<String, String> rparams = new HashMap<String, String>();
		rparams.put("_json_att", "");
		String response = HttpClientUtils.sendPost(url, rparams, "UTF-8", HeaderUtils.buildHeaders(headers));
		//System.err.println(response);
		//parse globalRepeatSubmitToken = 'a5fc4a460b481981bc64f6d319d3d899';
		//parse leftTicketStr = 'FefSoECHBbNpQzYGKFGQyDgSAu%2BL5JDGleppF6s7yBhhFMJBuerjOeFlYRk%3D'
		//parse key_check_isChange = 'BEC4BA187E85F01803BF0D7C653409F316B6E6D30F6BA423FCE7FAFC'
		Map<String, String> parseResult = parseResult(response);
		System.err.println(parseResult);
		params.put("globalRepeatSubmitToken", parseResult.get("globalRepeatSubmitToken"));
		try {
			params.put("leftTicketStr", URLDecoder.decode(parseResult.get("leftTicketStr"), "UTF-8"));
			params.put("keyCheckIsChange", URLDecoder.decode(parseResult.get("key_check_isChange"), "UTF-8"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		params.put("trainNo", parseResult.get("train_no"));
		params.put("trainDate", parseResult.get("train_date"));
		params.put("trainLocation", parseResult.get("train_location"));
		params.put("stationTrainCode", parseResult.get("station_train_code"));
		params.put("fromStationTelecode", parseResult.get("from_station_telecode"));
		params.put("toStationTelecode", parseResult.get("to_station_telecode"));
		return params;
	}
		
	static List<String> filterTokens = Arrays.asList(new String[]{"'train_date':\\{('\\w+':\\w+,){7}", 
		"'train_no':'\\w+'", "'train_location':'\\w+'", "'station_train_code':'\\w+'", 
			"'from_station_telecode':'\\w+'", "'to_station_telecode':'\\w+'", "'key_check_isChange':'\\w+'", 
				"'leftTicketStr':'[\\w%]+'", "globalRepeatSubmitToken = '\\w+';"});
	static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT+0800 (中国标准时间)'", Locale.US);  
	
	static {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	private Map<String, String> parseResult(String html) {
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0, len = filterTokens.size(); i < len; i++) {
			String token = filterTokens.get(i);
			Pattern pattern = Pattern.compile(token);
			Matcher matcher = pattern.matcher(html);
			if (matcher.find()) {
				String content = matcher.group().replaceAll("'", "").trim();
				String key = null, value = null;
				if (content.startsWith("train_date")) {
					key = "train_date";
					String[] words = content.split(":");
					Calendar calendar = Calendar.getInstance();  
					calendar.setTimeInMillis(Long.parseLong(words[words.length - 1].replace(",", "")));
				    value = sdf.format(calendar.getTime());  
				} else if (content.startsWith("globalRepeatSubmitToken")) {
					key = "globalRepeatSubmitToken";
					value = content.split("=")[1].replace(";", "");
				} else {
					String[] words = content.split(":");
					key = words[0];
					value = words[1];
				}
				result.put(key, value.trim());
			}
		}
		return result;
	}

}
