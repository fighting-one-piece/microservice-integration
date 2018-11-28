package org.cisiondata.utils.clazz;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.StringUtils;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanMap;

public class BeanUtil {
	
	@SuppressWarnings("rawtypes")
	public static Object directConvert(String value, Class clazz) {
		if (clazz.equals(String.class)) return value;
		String className = clazz.getName();
		if (className.equals("int")) {
			if (value == null || value.trim().length() == 0) return 0;
			if (value.contains(".")) value = value.substring(0, value.indexOf("."));
			return Integer.parseInt(value);
		}
		if (clazz.equals(Integer.class)) {
			if (value == null || value.trim().length() == 0) return null;
			if (value.contains(".")) value = value.substring(0, value.indexOf("."));
			return Integer.parseInt(value);
		}
		if (className.equals("double")) {
			return value == null || value.trim().length() == 0 ? 0D : Double.parseDouble(value);
		}
		if (clazz.equals(Double.class)) {
			return value == null || value.trim().length() == 0 ? null : Double.parseDouble(value);
		}
		if (className.equals("short")) {
			if (value == null || value.trim().length() == 0) return 0;
			if (value.contains(".")) value = value.substring(0, value.indexOf("."));
			return Short.parseShort(value);
		}
		if (clazz.equals(Short.class)) {
			if (value == null || value.trim().length() == 0) return null;
			if (value.contains(".")) value = value.substring(0, value.indexOf("."));
			return Short.parseShort(value);
		}
		if (className.equals("long")) {
			if (value == null || value.trim().length() == 0) return 0L;
			if (value.contains(".")) value = value.substring(0, value.indexOf("."));
			return Long.parseLong(value);
		}
		if (clazz.equals(Long.class)) {
			if (value == null || value.trim().length() == 0) return null;
			if (value.contains(".")) value = value.substring(0, value.indexOf("."));
			return Long.parseLong(value);
		}
		if (className.equals("float")) {
			return value == null || value.trim().length() == 0 ? 0 : Float.parseFloat(value);
		}
		if (clazz.equals(Float.class)) {
			return value == null || value.trim().length() == 0 ? null : Float.parseFloat(value);
		}
		if (clazz.equals(BigDecimal.class)) {
			return value == null || value.trim().length() == 0 ? null : new BigDecimal(value);
		}
		if (className.equals("boolean")) {
			if (value == null || value.trim().length() == 0) return false;
			if (value.matches("(?i)yes|true")) {
				return true;
			} else if (value.matches("(?i)no|false")) {
				return false;
			} else if (value.matches("\\d+")) {
				return Long.parseLong(value) == 0  ? false : true;
			}
			return Boolean.parseBoolean(value);
		}
		if (clazz.equals(Boolean.class)) {
			if (value == null || value.trim().length() == 0) return null;
			if (value.matches("(?i)yes|true")) {
				return true;
			} else if (value.matches("(?i)no|false")) {
				return false;
			} else if (value.matches("\\d+")) {
				return Long.parseLong(value) == 0  ? false : true;
			}
			return Boolean.parseBoolean(value);
		}
		return null;
	}

	private static net.sf.cglib.core.Converter converter = new net.sf.cglib.core.Converter() {
		@SuppressWarnings("rawtypes")
		public Object convert(Object value, Class target, Object context) {
			if (value != null && target != value.getClass()) {
				return directConvert(value.toString(), target);
			}
			return value;
		}
	};

	@SuppressWarnings("unchecked")
	public static void copyProperties(Object src, Object target, String... ignoreProperties) {
		if (ignoreProperties.length == 0) {
			BeanCopier copy = BeanCopier.create(src.getClass(), target.getClass(), true);
			copy.copy(src, target, converter);
		} else {
			BeanMap bm = BeanMap.create(src);
			bm.setBean(src);
			BeanMap bm2 = BeanMap.create(target);
			bm2.setBean(target);
			copyMapToBeanMap(bm, bm2, true, ignoreProperties);
		}
	}

	@SuppressWarnings("unchecked")
	public static void copyNotNullProperties(Object src, Object target, String... ignoreProperties) {
		BeanMap bm = BeanMap.create(src);
		bm.setBean(src);
		BeanMap bm2 = BeanMap.create(target);
		bm2.setBean(target);
		copyMapToBeanMap(bm, bm2, false, ignoreProperties);
	}

	@SuppressWarnings({ "rawtypes" })
	private static void copyMapToBeanMap(Map<String, Object> src, BeanMap target, boolean includeNull, String... ignoreProperties) {
		List<String> ignoreList = Arrays.asList(ignoreProperties);
		for (Entry<String, Object> en : src.entrySet()) {
			String key = en.getKey();
			if (ignoreList.contains(key)) continue;
			Object value = en.getValue();
			if (value == null && !includeNull) continue;
			if (target.containsKey(key)) {
				Class dstClazz = target.getPropertyType(key);
				if (value != null && dstClazz != value.getClass()) {
					target.put(key, directConvert(value.toString(), dstClazz));
					continue;
				}
				if (value == null && dstClazz.isPrimitive()) {
					continue;
				}
				target.put(key, value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromBean(Object obj) {
		BeanMap bm = BeanMap.create(obj);
		bm.setBean(obj);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(bm);
		return ret;
	}

	@SuppressWarnings("rawtypes")
	public static void copyRequestMap(Map<String, String[]> src, Object target, String joinString, String... ignoreProperties) {
		if (joinString == null) {
			joinString = ",";
		}
		BeanMap bm = BeanMap.create(target);
		bm.setBean(target);
		List<String> ignoreList = Arrays.asList(ignoreProperties);
		for (Entry<String, String[]> en : src.entrySet()) {
			String key = en.getKey();
			if (ignoreList.contains(key)) continue;
			String[] values = en.getValue();
			if (bm.containsKey(key) && values != null) {
				Object obj = null;
				Class dstClazz = bm.getPropertyType(key);
				if (!dstClazz.isArray()) {
					obj = directConvert(StringUtils.arrayToDelimitedString(values, joinString), dstClazz);
				} else if (dstClazz.isArray()) {
					obj = Array.newInstance(dstClazz.getComponentType(), values.length);
					for (int i = 0; i < values.length; i++) {
						Object tmp = directConvert(values[i], dstClazz.getComponentType());
						Array.set(obj, i, tmp);
					}
				}
				bm.put(key, obj);
			}
		}
	}

	public static void copyProperties(Map<String, Object> src, Object target, String... ignoreProperties) {
		BeanMap bm = BeanMap.create(target);
		bm.setBean(target);
		copyMapToBeanMap(src, bm, true, ignoreProperties);
	}

	public static void copyProperties(Map<String, Object> src, Map<String, Object> target, String... ignoreProperties) {
		List<String> ignoreList = Arrays.asList(ignoreProperties);
		for (Entry<String, Object> en : src.entrySet()) {
			String key = en.getKey();
			if (ignoreList.contains(key)) continue;
			Object value = en.getValue();
			target.put(key, value);
		}
	}
}
