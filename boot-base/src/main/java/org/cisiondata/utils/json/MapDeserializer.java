package org.cisiondata.utils.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

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
			map.put(entry.getKey(), handle(entry.getValue()));
		}
		return map;
	}

	/**
	 * 判断是不是数字
	 * @param input
	 * @return 是数字类型返回true
	 */
	public boolean isNumberic(String input) {
		Pattern pattern = Pattern.compile("\\d{1,18}|[0-8][0-9]{18}|9[0-1]\\d{17}");
		return !pattern.matcher(input).matches() ? false : true;
	}
	
	/**
	 * 递归处理Element
	 * @param element
	 * @return
	 */
	private Object handle(JsonElement element) {
		if (null == element || element instanceof JsonNull) return null;
		Class<?> elementClass = element.getClass();
		if (JsonObject.class.isAssignableFrom(elementClass)) {
			JsonObject jsonObject = element.getAsJsonObject();
			Map<String, Object> map = new HashMap<String, Object>();
			for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				map.put(entry.getKey(), handle(entry.getValue()));
			}
			return map;
		} else if (JsonArray.class.isAssignableFrom(elementClass)) {
			List<Object> list = new ArrayList<Object>();
			Iterator<JsonElement> iterator = element.getAsJsonArray().iterator();
			while (iterator.hasNext()) {
				JsonElement subElement = iterator.next();
				list.add(handle(subElement));
			}
			return list;
		} else {
			JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
			if (jsonPrimitive.isString()) {
				return element.getAsString();
			} else if (jsonPrimitive.isBoolean()) {
				return element.getAsBoolean();
			} else if (jsonPrimitive.isNumber()) {
				if (element.getAsString().indexOf(".") != -1) {
					return element.getAsDouble();
				} else {
					return element.getAsLong();
				}
			} else {
				return isNumberic(element.getAsString()) ? element.getAsLong() : element.getAsString();
			}
		}
	}

}
