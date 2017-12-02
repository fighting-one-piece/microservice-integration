package org.cisiondata.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cisiondata.utils.date.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(ReflectUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getParameterizedType(Class<?> clazz, int index) {
        Type parameterizedType = clazz.getGenericSuperclass();
        if (!(parameterizedType instanceof ParameterizedType)) {
            parameterizedType = clazz.getSuperclass().getGenericSuperclass();
        }
        if (!(parameterizedType instanceof  ParameterizedType)) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
        if (actualTypeArguments == null || actualTypeArguments.length == 0) {
            return null;
        }
        return (Class<T>) actualTypeArguments[0];
    }
	
	public static Field[] getFields(Object object) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
        	for (Field field : superClass.getDeclaredFields()) {
        		fields.add(field);
        	}
        }
		return fields.toArray(new Field[0]);
	}
	
	public static Field[] getFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
        	for (Field field : superClass.getDeclaredFields()) {
        		fields.add(field);
        	}
        }
		return fields.toArray(new Field[0]);
	}
	
	public static Field getFieldByFieldName(Object object, String fieldName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
        	Field[] fields = superClass.getDeclaredFields();
    		for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
    			if (fieldName.equalsIgnoreCase(field.getName())) {
					return field;
    			}
    		}
        }
        return null;
    }

    public static Object getValueByFieldName(Object object, String fieldName) {
        Field field = getFieldByFieldName(object, fieldName);
        Object value = null;
        try {
		    if(null != field){
		        if (field.isAccessible()) {
		            value = field.get(object);
		        } else {
		            field.setAccessible(true);
		            value = field.get(object);
		            field.setAccessible(false);
		        }
		    }
        } catch (Exception e) {
        	LOG.info(e.getMessage(), e);
        }
        return value;
    }

    public static void setValueByFieldName(Object object, String fieldName, Object value) {
    	Field field = getFieldByFieldName(object, fieldName);
    	if (null == field || Modifier.isStatic(field.getModifiers())) return;
    	try {
    		value = convertValueByFileType(field.getType(), value);
	        if (field.isAccessible()) {
	            field.set(object, value);
	        } else {
	            field.setAccessible(true);
	            field.set(object, value);
	            field.setAccessible(false);
	        }
    	} catch (Exception e) {
        	LOG.info(e.getMessage(), e);
        }
    }
    
    public static Object convertValueByFileType(Class<?> type, Object value) {
    	Object finalValue = value;
    	if (String.class.isAssignableFrom(type)) {
    		finalValue = String.valueOf(value);
		} else if (Boolean.class.isAssignableFrom(type)) {
    		finalValue = Boolean.parseBoolean(String.valueOf(value));
		} else if (Integer.class.isAssignableFrom(type)) {
    		finalValue = Integer.parseInt(String.valueOf(value));
		} else if (Long.class.isAssignableFrom(type)) {
			finalValue = Long.parseLong(String.valueOf(value));
		} else if (Float.class.isAssignableFrom(type)) {
			finalValue = Float.parseFloat(String.valueOf(value));
		} else if (Double.class.isAssignableFrom(type)) {
			finalValue = Double.parseDouble(String.valueOf(value));
		} else if (Date.class.isAssignableFrom(type)) {
			try {
				finalValue = DateFormatter.TIME.get().parse(String.valueOf(value));
			} catch (ParseException e) {
				LOG.error(e.getMessage(), e);
			}
		} 
    	return finalValue;
    }
    
    public static boolean isExistField(Object object, String fieldName) {
    	try {
    		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            	Field[] fields = superClass.getDeclaredFields();
        		for (int i = 0, len = fields.length; i < len; i++) {
    				Field field = fields[i];
        			if (fieldName.equals(field.getName())) {
    					return true;
        			}
        		}
            }
		} catch (Exception e) {
        	LOG.info(e.getMessage(), e);
        }
    	return false;
    }
    
    public static boolean isExistMethod(Object object, String methodName) {
    	try {
			for (Method method : object.getClass().getMethods()) {
				if (methodName.equals(method.getName())) {
					return true;
				}
			}
		} catch (Exception e) {
        	LOG.info(e.getMessage(), e);
        }
    	return false;
    }
    
    public static Object getValueByMethodName(Object object, String methodName) {
    	return getValueByMethodName(object, null, methodName);
	}
    
    public static Object getValueByMethodName(Object object, String methodName, Class<?>... parameterTypes) {
		return getValueByMethodName(object, null, methodName, parameterTypes);
	}
    
    public static Object getValueByMethodName(Object object, Object values[], String methodName, Class<?>... parameterTypes) {
		if (!isExistMethod(object, methodName)) return null;
		try {
			return object.getClass().getMethod(methodName, parameterTypes).invoke(object, values);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return null;
	}
    
    public static Map<String, Object> convertObjectToObjectMap(Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = getFields(object);
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) continue;
				String name = field.getName();
				field.setAccessible(true);
				Object value = field.get(object);
				field.setAccessible(false);
				if (null == value) continue;
				map.put(name, value);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return map;
	}
    
    public static void convertObjectMapToObject(Map<String, Object> map, Object object) {
    	Field[] fields = getFields(object);
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) continue;
				String name = field.getName();
				Object value = map.get(name);
				if (null == value || !field.getType().equals(value.getClass())) continue;
				field.setAccessible(true);
				field.set(object, value);
				field.setAccessible(false);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
    }
    
    public static Map<String, String> convertObjectToStringMap(Object object) {
		Map<String, String> map = new HashMap<String, String>();
		Field[] fields = getFields(object);
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) continue;
				String name = field.getName();
				field.setAccessible(true);
				Object value = field.get(object);
				field.setAccessible(false);
				if (null == value) continue;
				String finalValue = String.valueOf(value);
				Class<?> type = field.getType();
				if (Date.class.isAssignableFrom(type)) {
					finalValue = DateFormatter.TIME.get().format(value);
				} 
				map.put(name, finalValue);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return map;
	}
    
    public static void convertStringMapToObject(Map<String, String> map, Object object) {
    	Field[] fields = getFields(object);
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) continue;
				String name = field.getName();
				String valueString = map.get(name);
				if (null == valueString) continue;
				Object value = convertValueByFileType(field.getType(), valueString);
				field.setAccessible(true);
				field.set(object, value);
				field.setAccessible(false);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
    }
}
