package org.platform.utils.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.platform.modules.elastic.entity.BoolCondition;
import org.platform.modules.elastic.entity.Condition;
import org.platform.modules.elastic.entity.TermCondition;
import org.platform.utils.reflect.ReflectUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;  

public class ConditionDeserializer implements JsonDeserializer<Condition> {
	
	public Condition deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return convertToCondition(json.getAsJsonObject());
	}
	
	private Condition convertToCondition(JsonObject jsonObject) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		Condition condition = null;
		String entityName = ((JsonElement) map.get("entityName")).getAsString();
		if (TermCondition.class.getSimpleName().equalsIgnoreCase(entityName)) {
			condition = convertToTermCondition(map);
		} else if (BoolCondition.class.getSimpleName().equalsIgnoreCase(entityName)) {
			condition = convertToBoolCondition(map);
		}
		return condition;
	}
	
	private TermCondition convertToTermCondition(Map<String, Object> map) {
		TermCondition termCondition = new TermCondition();
		Map<String, String> stringMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			stringMap.put(entry.getKey(), ((JsonElement) entry.getValue()).getAsString());
		}
		ReflectUtils.convertStringMapToObject(stringMap, termCondition);
		return termCondition;
	}
	
	private BoolCondition convertToBoolCondition(Map<String, Object> map) {
		BoolCondition boolCondition = new BoolCondition();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			JsonElement element = (JsonElement) entry.getValue();
			Class<?> elementClass = element.getClass();
			if (JsonArray.class.isAssignableFrom(elementClass)) {
				List<Condition> conditions = new ArrayList<Condition>();
				JsonArray jsonArray = element.getAsJsonArray();
				Iterator<JsonElement> elements = jsonArray.iterator();
				while (elements.hasNext()) {
					JsonObject jsonObject = elements.next().getAsJsonObject();
					conditions.add(convertToCondition(jsonObject));
				}
				ReflectUtils.setValueByFieldName(boolCondition, entry.getKey(), conditions);
			}
		}
		return boolCondition;
	}
	
}
