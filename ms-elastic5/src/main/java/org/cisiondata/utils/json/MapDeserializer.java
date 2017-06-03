package org.cisiondata.utils.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;  

public class MapDeserializer implements JsonDeserializer<Map<String, Object>> {

	/**
	 * 默认会将Object接收的数字转换为double 
	 * int类型12转换后会变成12.0，自定义转换的目的就是将他转换为12
	 */
	public Map<String, Object> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		Map<String, Object> map = new HashMap<String, Object>();
		Set<Entry<String, JsonElement>> entries = json.getAsJsonObject().entrySet();
		for (Entry<String, JsonElement> entry : entries) {
			JsonElement element = entry.getValue();
			if (null == element || element instanceof JsonNull) continue;
			Class<?> elementClass = element.getClass();
			if (JsonArray.class.isAssignableFrom(elementClass)) {
				map.put(entry.getKey(), GsonUtils.builder().toJson(element));
			} else if (JsonObject.class.isAssignableFrom(elementClass)) {
				map.put(entry.getKey(), GsonUtils.builder().toJson(element));
			} else {
				/**
				map.put(entry.getKey(), isNumberic(element.getAsString()) ? element.getAsLong() : element.getAsString());
				*/
				map.put(entry.getKey(), element.getAsString());
			}
		}
		return map;
	}

	/**
	 * 判断是不是数字
	 * @param input
	 * @return 是数字类型返回true
	 */
	public boolean isNumberic(String input) {
		if (StringUtils.isNotBlank(input) && input.length() > 19) return false;
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(input);
		return !matcher.matches() ? false : true;
	}

}
