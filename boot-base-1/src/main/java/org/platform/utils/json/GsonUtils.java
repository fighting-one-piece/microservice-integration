package org.platform.utils.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.platform.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GsonUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(GsonUtils.class);

	private static Gson gson = null;
	
	static {
		gson = new GsonBuilder()
				.serializeSpecialFloatingPointValues()
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.registerTypeAdapter(new TypeToken<List<Object>>(){}.getType(), new ListDeserializer())
				.registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(), new MapDeserializer())
				.create();
	}
	
	public static Gson builder() {
		return gson;
	}
	
	/**
	 * List 转换 JSON
	 * @param list
	 * @return
	 */
	public static <T> String fromListToJson(List<T> list) {
		return builder().toJson(list, new TypeToken<List<Object>>(){}.getType());
	}
	
	/** 
	 * JSON 转换  List, 仅限于list里面为基础对象
	 * @param json
	 * @return
	 */
	public static <T> List<T> fromJsonToList(String json) {
		return builder().fromJson(json, new TypeToken<List<Object>>(){}.getType());
	}
	
	/** 
	 * JSON 转换  List<?>, list里面为实体对象
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromJsonToList(String json, Class<?> entityClass) {
		List<T> entities = new ArrayList<T>();
		try {
			List<Object> ts = builder().fromJson(json, new TypeToken<List<Object>>(){}.getType());
			for (int i = 0, len = ts.size(); i < len; i++) {
				Map<String, String> map = (Map<String, String>) ts.get(i);
				Object entity = entityClass.newInstance();
				ReflectUtils.convertStringMapToObject(map, entity);
				entities.add((T) entity);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return entities;
	}
	
	/** 
	 * JSON 转换  Map
	 * @param json
	 * @return
	 */
	public static Map<String, Object> fromJsonToMap(String json) {
		return builder().fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
	}
	
	/** 
	 * JSON 转换  Map
	 * @param json
	 * @return
	 */
	public static Map<String, ?> fromJsonToMapExt(String json) {
		return builder().fromJson(json, new TypeToken<Map<String, ?>>(){}.getType());
	}
	
	/**
	 * Map 转换 JSON
	 * @param map
	 * @return
	 */
	public static String fromMapToJson(Map<String, Object> map) {
		return builder().toJson(map, new TypeToken<Map<String, Object>>(){}.getType());
	}
	
	/**
	 * Map 转换 JSON
	 * @param map
	 * @return
	 */
	public static String fromMapExtToJson(Map<String, ?> map) {
		return builder().toJson(map, new TypeToken<Map<String, ?>>(){}.getType());
	}
	
}
