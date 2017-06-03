package org.cisiondata.modules.abstr.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cisiondata.modules.abstr.entity.CEntity;
import org.cisiondata.modules.abstr.entity.CEntityData;
import org.cisiondata.utils.date.DateFormatter;
import org.cisiondata.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(EntityUtils.class);
	
	private static Set<String> basicAttributes = null;
	
	static {
		basicAttributes = new HashSet<String>();
		Field[] fields = CEntity.class.getDeclaredFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				String name = fields[i].getName();
				if ("serialVersionUID".equalsIgnoreCase(name)) continue;
				basicAttributes.add(name);
			}
			basicAttributes.add("createTimeGT");
			basicAttributes.add("createTimeGE");
			basicAttributes.add("createTimeLT");
			basicAttributes.add("createTimeLE");
			basicAttributes.add("ids");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public static String entityTable(Class<?> clazz) {
		return "T_ENTITY_" + clazz.getSimpleName().toUpperCase();
	}
	
	public static String entityDataTable(Class<?> clazz) {
		return "T_ENTITY_DATA_" + clazz.getSimpleName().toUpperCase();
	}
	
	public static String entityRelationTable(Class<?> clazz) {
		return "T_ENTITY_RELATION" + clazz.getSimpleName().toUpperCase();
	}
	
	public static boolean isBasicAttribute(String attribute) {
		return basicAttributes.contains(attribute) ? true : false;
	}
	
	public static boolean isNeedUpdate(CEntity entity) {
		boolean idFlag = false, fieldFlag = false;
		Field[] fields = CEntity.class.getDeclaredFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				String name = field.getName();
				if ("serialVersionUID".equalsIgnoreCase(name) ||
						"table".equalsIgnoreCase(name)) continue;
				field.setAccessible(true);
				Object value = field.get(entity);
				field.setAccessible(false);
				if (null == value) continue;
				if ("id".equalsIgnoreCase(name)) {
					idFlag = true;
				} else {
					fieldFlag = true;
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return idFlag && fieldFlag ? true : false;
	}
	
	public static Long extractId(Object object) {
		Field[] superFields = object.getClass().getSuperclass().getDeclaredFields();
		try {
			for (int i = 0, len = superFields.length; i < len; i++) {
				Field field = superFields[i];
				String name = field.getName();
				if (!"id".equalsIgnoreCase(name)) continue;
				field.setAccessible(true);
				Object value = field.get(object);
				field.setAccessible(false);
				return (Long) value;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return null;
	}
	
	public static CEntity extractEntity(Object object) {
		CEntity entity = new CEntity();
		Field[] fields = CEntity.class.getDeclaredFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) continue;
				String name = field.getName();
				if ("serialVersionUID".equalsIgnoreCase(name)) continue;
				Field objField = object.getClass().getSuperclass().getDeclaredField(name);
				if (null == objField) continue;
				objField.setAccessible(true);
				Object objFieldValue = objField.get(object);
				objField.setAccessible(false);
				field.setAccessible(true);
				field.set(entity, objFieldValue);
				field.setAccessible(false);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		entity.setTable(entityTable(object.getClass()));
		return entity;
	}
	
	public static List<CEntityData> extractEntityDatas(Object object) {
		List<CEntityData> entityDatas = new ArrayList<CEntityData>();
		Field[] fields = object.getClass().getDeclaredFields();
		try {
			CEntityData entityData = null;
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers()) || 
						Modifier.isTransient(field.getModifiers())) continue;
				String name = field.getName();
				if ("serialVersionUID".equalsIgnoreCase(name)) continue;
				field.setAccessible(true);
				Object value = field.get(object);
				field.setAccessible(false);
				if (null == value) continue;
				entityData = new CEntityData();
				entityData.setTable(entityDataTable(object.getClass()));
				entityData.setEntityId(extractId(object));
				entityData.setAttribute(name);
				String[] valueAndKey = getValueAndKind(value);
				entityData.setValue(valueAndKey[0]);
				entityData.setKind(valueAndKey[1]);
				entityDatas.add(entityData);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return entityDatas;
	}
	
	public static Object convertEntityToObject(CEntity entity, Class<?> entityClass) {
		if (null == entity) return null;
		Object entityObject = null; 
		try {
			Map<String, Object> map = convertObjectToMap(entity);
			for (CEntityData entityData : entity.getDatas()) {
				map.put(entityData.getAttribute(), getValueByKind(entityData.getKind(), entityData.getValue()));
			}
			entityObject = entityClass.newInstance();
			convertMapToObject(map, entityObject);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return entityObject;
	}
	
	public static Object convertEntityToObject(CEntity entity, List<CEntityData> entityDatas, Class<?> entityClass) {
		Object entityObject = null;
		try {
			Map<String, Object> map = convertObjectToMap(entity);
			for (CEntityData entityData : entityDatas) {
				map.put(entityData.getAttribute(), getValueByKind(entityData.getKind(), entityData.getValue()));
			}
			entityObject = entityClass.newInstance();
			convertMapToObject(map, entityObject);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return entityObject;
	}
	
	public static Map<String, Object> convertObjectToMap(Object object) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == object) return map;
		Field[] fields = object.getClass().getDeclaredFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) continue;
				String name = field.getName();
				if ("serialVersionUID".equalsIgnoreCase(name) 
						|| "datas".equalsIgnoreCase(name)) continue;
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
	
	public static void convertMapToObject(Map<String, Object> map, Object object) {
		try {
			for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
	        	for (Field field : superClass.getDeclaredFields()) {
	        		if (Modifier.isStatic(field.getModifiers())) continue;
	        		String name = field.getName();
					if ("serialVersionUID".equalsIgnoreCase(name)) continue;
					Object value = map.get(name);
					if (null == value) continue;
					field.setAccessible(true);
					field.set(object, value);
					field.setAccessible(false);
	    		}
	        }
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public static Map<String, Object> convertJsonToMap(String json) {
		return (Map<String, Object>) GsonUtils.fromJsonToMap(json);
	}
	
	public static String convertMapToJson(Map<String, Object> map){
		if(map == null || map.size() == 0) return "";
		return GsonUtils.fromMapToJson(map);
	}
	
	public static Object getValueByFieldName(Object object, String fieldName) {
        Object value = null;
        try {
        	for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            	for (Field field : superClass.getDeclaredFields()) {
            		if(null == field || !fieldName.equals(field.getName())) continue;
			        if (field.isAccessible()) {
			            value = field.get(object);
			        } else {
			            field.setAccessible(true);
			            value = field.get(object);
			            field.setAccessible(false);
			        }
        		}
        	}
        } catch (Exception e) {
        	LOG.info(e.getMessage(), e);
        }
        return value;
    }
	
	public static void setValueByFieldName(Object object, String fieldName, Object value) {
		try {
			for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            	for (Field field : superClass.getDeclaredFields()) {
        			if (fieldName.equals(field.getName())) {
        				field.setAccessible(true);
        				field.set(object, value);
        				field.setAccessible(false);
        			}
        		}
            }
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public static void setValueBySetMethod(Object object, Object value, String methodName, Class<?>... parameterTypes) {
		try {
			object.getClass().getMethod(methodName, parameterTypes).invoke(object, value);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public static void setValuesBySetMethod(Object object, Object values[], String methodName, Class<?>... parameterTypes) {
		try {
			object.getClass().getMethod(methodName, parameterTypes).invoke(object, values);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public static Object getValueByMethodName(Object object, String methodName) {
		return getValueByMethodName(object, new Object[0], methodName, new Class[0]);
	}
	
	public static Object getValueByMethodName(Object object, Object values[], String methodName, Class<?>... parameterTypes) {
		Object returnValue = null;
		try {
			returnValue = object.getClass().getMethod(methodName, parameterTypes).invoke(object, values);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return returnValue;
	}
	
	public static Object getValueByKind(String kind, String value) {
		Object finalValue = value;
		if (kind.equalsIgnoreCase(Kind.BOOLEAN.getName())) {
    		finalValue = Boolean.parseBoolean(String.valueOf(value));
		} else if (kind.equalsIgnoreCase(Kind.INTEGER.getName())) {
			finalValue = Integer.parseInt(value);
		} else if (kind.equalsIgnoreCase(Kind.LONG.getName())) {
			finalValue = Long.parseLong(value);
		} else if (kind.equalsIgnoreCase(Kind.FLOAT.getName())) {
			finalValue = Float.parseFloat(value);
		} else if (kind.equalsIgnoreCase(Kind.DOUBLE.getName())) {
			finalValue = Double.parseDouble(value);
		} else if (kind.equalsIgnoreCase(Kind.DATE.getName())) {
			try {
				finalValue = DateFormatter.TIME.get().parse(value);
			} catch (ParseException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return finalValue;
	}
	
	public static String[] getValueAndKind(Object value) {
		String finalValue = String.valueOf(value);
		String kind = Kind.STRING.getName();
		if (value instanceof Boolean) {
			kind = Kind.BOOLEAN.getName();
		} else if (value instanceof Integer) {
			kind = Kind.INTEGER.getName();
		} else if (value instanceof Long) {
			kind = Kind.LONG.getName();
		} else if (value instanceof Float) {
			kind = Kind.FLOAT.getName();
		} else if (value instanceof Double) {
			kind = Kind.DOUBLE.getName();
		} else if (value instanceof Date) {
			finalValue = DateFormatter.TIME.get().format((Date) value);
			kind = Kind.DATE.getName();
		} 
		return new String[]{finalValue, kind};
	}
	
}
