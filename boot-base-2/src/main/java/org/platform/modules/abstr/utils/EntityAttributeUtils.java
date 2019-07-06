package org.platform.modules.abstr.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.platform.utils.date.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityAttributeUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(EntityAttributeUtils.class);
	
	public static final String PROP_ID = "id";
	public static final String PROP_KEY = "key";
	public static final String PROP_VALUE = "value";
	public static final String PROP_TYPE = "type";

	@SuppressWarnings("unchecked")
	public static <T> List<T> extractAttributes(Object entity, Class<?> attributeClass) {
		List<T> attributes = new ArrayList<T>();
		Field[] fields = entity.getClass().getDeclaredFields();
		try {
			T attribute = null;
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers()) || 
						!Modifier.isTransient(field.getModifiers())) continue;
				String name = field.getName();
				field.setAccessible(true);
				Object value = field.get(entity);
				field.setAccessible(false);
				if (null == value) continue;
				attribute = (T) attributeClass.newInstance();
				Field idField = getFieldByFieldNameSuffix(attribute, "Id");
				if (null != idField) {
					setValueByFieldName(attribute, idField.getName(), getValueByFieldName(entity, PROP_ID));
				}
				setValueByFieldName(attribute, PROP_KEY, name);
				String[] valueAndKey = extractValueAndKind(value);
				setValueByFieldName(attribute, PROP_VALUE, valueAndKey[0]);
				setValueByFieldName(attribute, PROP_TYPE, valueAndKey[1]);
				attributes.add(attribute);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return attributes;
	}
	
	public static <T> void fillEntity(List<T> attributes, Object entity) {
		if (null == attributes || attributes.size() == 0) return;
		for (int i = 0, len = attributes.size(); i < len; i++) {
			T attribute = attributes.get(i);
			setValueByFieldName(entity, String.valueOf(getValueByFieldName(attribute, PROP_KEY)), 
					extractValue(getValueByFieldName(attribute, PROP_TYPE), 
							getValueByFieldName(attribute, PROP_VALUE)));
		}
	}
	
	public static Object extractValue(Object kindObject, Object valueObject) {
		String kind = String.valueOf(kindObject);
		String value = String.valueOf(valueObject);
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

	public static String[] extractValueAndKind(Object value) {
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
	
	public static Field getFieldByFieldNameSuffix(Object object, String fieldNameSuffix) {
        try {
        	for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            	for (Field field : superClass.getDeclaredFields()) {
            		if (Modifier.isStatic(field.getModifiers()) || 
    						Modifier.isTransient(field.getModifiers())) continue;
            		if (field.getName().endsWith(fieldNameSuffix)) return field;
            	}
        	}
        } catch (Exception e) {
        	LOG.info(e.getMessage(), e);
        }
        return null;
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
			for (Class<?> superClass = object.getClass(); superClass != Object.class; 
					superClass = superClass.getSuperclass()) {
            	for (Field field : superClass.getDeclaredFields()) {
        			if (fieldName.equals(field.getName())) {
        				field.setAccessible(true);
        				field.set(object, value);
        				field.setAccessible(false);
        				break;
        			}
        		}
            }
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	public static boolean isNeedUpdateEntity(Object entity) {
		boolean fieldFlag = false;
		Field[] fields = entity.getClass().getDeclaredFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers()) || 
						Modifier.isTransient(field.getModifiers())) continue;
				field.setAccessible(true);
				Object value = field.get(entity);
				field.setAccessible(false);
				if (null == value) continue;
				fieldFlag = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return fieldFlag ? true : false;
	}
	
}
