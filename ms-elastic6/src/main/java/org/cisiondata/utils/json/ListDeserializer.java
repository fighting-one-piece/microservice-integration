package org.cisiondata.utils.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;  

public class ListDeserializer implements JsonDeserializer<List<Object>> {
	
	/**
	 * 默认会将Object接收的数字转换为double 
	 * int类型12转换后会变成12.0，自定义转换的目的就是将他转换为12
	 */
	public List<Object> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		List<Object> list = new ArrayList<Object>();
		Iterator<JsonElement> iterator = json.getAsJsonArray().iterator();
		while (iterator.hasNext()) {
			JsonElement element = iterator.next();
			if (null == element || element instanceof JsonNull) continue;
			Class<?> elementClass = element.getClass();
			if (JsonObject.class.isAssignableFrom(elementClass)) {
				JsonObject jsonObject = element.getAsJsonObject();
				Map<String, String> map = new HashMap<String, String>();
				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					map.put(entry.getKey(), entry.getValue().getAsString());
				}
				list.add(map);
			} else {
				list.add(isNumberic(element.getAsString()) ? element.getAsLong() : element);
			}
		}
		return list;
	}

	/**
	 * 判断是不是数字
	 * @param input
	 * @return 是数字类型返回true
	 */
	public boolean isNumberic(String input) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(input);
		return !matcher.matches() ? false : true;
	}

}
