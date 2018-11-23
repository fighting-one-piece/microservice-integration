package org.platform.modules.kyfw.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.platform.modules.kyfw.utils.HeaderUtils;
import org.platform.utils.http.HttpClientUtils;
import org.platform.utils.json.GsonUtils;

public class KyfwS10LeftTicket extends KyfwHandler {
	
	@Override
	public Map<String, String> handle(Map<String, String> params) {
		String url = "https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
		headers.put("Cookie", buildCookie(params));
		url = String.format(url, "2018-01-15", "CDW", "BJP");
		String response = HttpClientUtils.sendGet(url, HeaderUtils.buildHeaders(headers));
		System.err.println(response);
		//{"validateMessagesShowId":"_validatorMessage","status":true,"httpstatus":200,"data":{"result":["pCmyJq9Om7sylLwJt9GzIoVFsoK41gDUgD6R8ryeatUEiT1qfl9tL420tINbuM%2BwdZJaxO5rwhHR%0AKX2jHR6Uq9T7xEEOIuC80vt9fFXpXJXOhL3npmj47vrvovn5PnQE0LxuRid1S8KlrFdEhlTjff%2F%2F%0AC5%2B62LsvF7Ez%2BdWSE%2FHgrX2Qrfj8zyz3iL7Usab4N3RTBNzhH1kjAiqUxPsnS1fhqIT%2FmatIZmMy%0AbYqUW4b%2BZeDAFUwDkC5zuqXl%2BCg4TtNlQz4xDhE%3D|预订|7600000Z5003|Z50|CDW|BXP|CDW|BXP|11:42|10:02|22:20|Y|FefSoECHBbNpQzYGKFGQyDgSAu%2BL5JDGleppF6s7yBhhFMJBuerjOeFlYRk%3D|20171221|3|W1|01|11|0|0||||有|||无||有|无|||||10401030|1413|0","nBL02cVpclaQt4ZWBgYwEFbnqP25rWHfh9dtkbM3%2BMpcCVuGzd8PxTJAssg1qqRFDONA7Ekxu6aW%0ASModF0LGd178ExHmP91cEqr3yhAfv0K%2BCllm3c4gBxjJGSL6yQt5918CwOYRhZIRYmEOLPvDVaP7%0ACAZt6QisRMq9vnZCiDqKJURUPAtPXo6X55Uwp7tinIye%2BgL1x0emrd6IlfrvH%2FfZ5dYX5UP%2B%2FgcS%0AizwSxoKDE3WlD6G%2BwzCHU9sepnSeShVIK7LcEyM%3D|预订|760000K8180B|K818|CDW|BXP|CDW|BXP|19:30|21:06|25:36|Y|Pko7mXVDi6qNbn4EidgP572hQFXWjWUb%2BZo8jlFNdD3SLEj3sMb872oT8bY%3D|20171221|3|W2|01|17|0|0||||有|||无||有|有|||||10401030|1413|0","m4LtEQlxCqNpnMaAHfUpmEwsgdSm%2FjWydZZjUgWPDMsbAHZKCx8mjRYST%2BTDW0rMPcc4eFYAa8BR%0A7gUNRBzJO%2F8qrtpC283ZDxnqEbO0chGZMJ6I8AnR9OjSmrAcLIpn3vlfSF%2Bs9xm23R1hWjhWoILR%0AzIYjaUqUUIPyE9SzDqgrnyaAp0aE2aFVkrz5OKDg%2BIm860IfP9JevcU9D1N4HIRjJC1CIDKQl%2Fi6%0Ap%2FPhpsFjQ%2FjTK4iPVzFAynsqGM7vVga3awBpu%2FI%3D|预订|76000K136409|K1364|CDW|BXP|CDW|BXP|22:00|05:42|31:42|Y|UoUVRCBE6Zp2r8Gx7V9Q8pR9SDOB6vkabr%2F2jN73IDkUX4MStj9hjOfsVQo%3D|20171221|3|W1|01|24|0|0||||有|||无||有|有|||||10401030|1413|1","voQhY3lkobvZW0pGY46q1%2F6bHxAWKCD9Qw%2FkG%2FpetuysEpKGB3eS3wLsF1l8%2BW%2FcwB8HJPVRBpjs%0A79OSECEm1lr8zhMXc0oRiDgl1RgExZ3YMv6vseaMhYDB4VGkbl3kEYqScfPls45S3IJYbdHHXQjZ%0AIWuOtGZ%2BF6r88lvZOUxj6u7QVHvdYz2lWkW0HgMf%2FSL8f4ABgEEt10k9uqsWTNsnOdlOaLbLx4jl%0Ax0NWH28E9DZK6CgrDr8sR53i7uUHB4Oawyw6fWI%3D|预订|760000K1180A|K118|PRW|BXP|CDW|BXP|23:59|05:10|29:11|Y|d5Mq7XWmfneb9KLe3mGU7aFJquWf7J97AEq00LYoOv7Wa6FY9YZxBe2nNG4%3D|20171221|3|W2|08|28|0|0||||18|||无||有|有|||||10401030|1413|1"],"flag":"1","map":{"BXP":"北京西","CDW":"成都"}},"messages":[],"validateMessages":{}}
		params.put("secretStr", extractSecretStr(response));
		params.put("trainDate", "2018-01-15");
		params.put("fromStationName", "成都");
		params.put("toStationName", "北京");
		return params;
	}
	
	public String extractSecretStr(String json) {
		if (!json.startsWith("{") || !json.endsWith("}")) return null;
		String secretStr = null;
		Map<String, Object> result = GsonUtils.fromJsonToMap(json);
		Object dataObj = result.get("data");
		if (null != dataObj) {
			Map<String, Object> data = GsonUtils.fromJsonToMap(String.valueOf(dataObj));
			Object dresult = data.get("result");
			if (null != dresult) {
				List<Object> list = GsonUtils.fromJsonToList(String.valueOf(dresult));
				secretStr = String.valueOf(list.get(0)).split("\\|")[0].substring(1);
				/**
				for (int i = 0, len = list.size(); i < len; i++) {
					String[] record = String.valueOf(list.get(i)).split("\\|");
					secretStr = record[0];
				}
				*/
			}
		}
		return secretStr;
	}
	//"pCmyJq9Om7sylLwJt9GzIoVFsoK41gDUgD6R8ryeatUEiT1qfl9tL420tINbuM%2BwdZJaxO5rwhHR%0AKX2jHR6Uq9T7xEEOIuC80vt9fFXpXJXOhL3npmj47vrvovn5PnQE0LxuRid1S8KlrFdEhlTjff%2F%2F%0AC5%2B62LsvF7Ez%2BdWSE%2FHgrX2Qrfj8zyz3iL7Usab4N3RTBNzhH1kjAiqUxPsnS1fhqIT%2FmatIZmMy%0AbYqUW4b%2BZeDAFUwDkC5zuqXl%2BCg4TtNlQz4xDhE%3D|预订|7600000Z5003|Z50|CDW|BXP|CDW|BXP|11:42|10:02|22:20|Y|FefSoECHBbNpQzYGKFGQyDgSAu%2BL5JDGleppF6s7yBhhFMJBuerjOeFlYRk%3D|20171221|3|W1|01|11|0|0||||有|||无||有|无|||||10401030|1413|0"
	//"nBL02cVpclaQt4ZWBgYwEFbnqP25rWHfh9dtkbM3%2BMpcCVuGzd8PxTJAssg1qqRFDONA7Ekxu6aW%0ASModF0LGd178ExHmP91cEqr3yhAfv0K%2BCllm3c4gBxjJGSL6yQt5918CwOYRhZIRYmEOLPvDVaP7%0ACAZt6QisRMq9vnZCiDqKJURUPAtPXo6X55Uwp7tinIye%2BgL1x0emrd6IlfrvH%2FfZ5dYX5UP%2B%2FgcS%0AizwSxoKDE3WlD6G%2BwzCHU9sepnSeShVIK7LcEyM%3D|预订|760000K8180B|K818|CDW|BXP|CDW|BXP|19:30|21:06|25:36|Y|Pko7mXVDi6qNbn4EidgP572hQFXWjWUb%2BZo8jlFNdD3SLEj3sMb872oT8bY%3D|20171221|3|W2|01|17|0|0||||有|||无||有|有|||||10401030|1413|0"
	//"m4LtEQlxCqNpnMaAHfUpmEwsgdSm%2FjWydZZjUgWPDMsbAHZKCx8mjRYST%2BTDW0rMPcc4eFYAa8BR%0A7gUNRBzJO%2F8qrtpC283ZDxnqEbO0chGZMJ6I8AnR9OjSmrAcLIpn3vlfSF%2Bs9xm23R1hWjhWoILR%0AzIYjaUqUUIPyE9SzDqgrnyaAp0aE2aFVkrz5OKDg%2BIm860IfP9JevcU9D1N4HIRjJC1CIDKQl%2Fi6%0Ap%2FPhpsFjQ%2FjTK4iPVzFAynsqGM7vVga3awBpu%2FI%3D|预订|76000K136409|K1364|CDW|BXP|CDW|BXP|22:00|05:42|31:42|Y|UoUVRCBE6Zp2r8Gx7V9Q8pR9SDOB6vkabr%2F2jN73IDkUX4MStj9hjOfsVQo%3D|20171221|3|W1|01|24|0|0||||有|||无||有|有|||||10401030|1413|1"
	//"voQhY3lkobvZW0pGY46q1%2F6bHxAWKCD9Qw%2FkG%2FpetuysEpKGB3eS3wLsF1l8%2BW%2FcwB8HJPVRBpjs%0A79OSECEm1lr8zhMXc0oRiDgl1RgExZ3YMv6vseaMhYDB4VGkbl3kEYqScfPls45S3IJYbdHHXQjZ%0AIWuOtGZ%2BF6r88lvZOUxj6u7QVHvdYz2lWkW0HgMf%2FSL8f4ABgEEt10k9uqsWTNsnOdlOaLbLx4jl%0Ax0NWH28E9DZK6CgrDr8sR53i7uUHB4Oawyw6fWI%3D|预订|760000K1180A|K118|PRW|BXP|CDW|BXP|23:59|05:10|29:11|Y|d5Mq7XWmfneb9KLe3mGU7aFJquWf7J97AEq00LYoOv7Wa6FY9YZxBe2nNG4%3D|20171221|3|W2|08|28|0|0||||18|||无||有|有|||||10401030|1413|1"

}
