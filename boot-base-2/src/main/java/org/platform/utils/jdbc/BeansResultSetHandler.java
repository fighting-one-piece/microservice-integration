package org.platform.utils.jdbc;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;

import org.platform.utils.reflect.ReflectUtils;

public class BeansResultSetHandler extends AbstrResultSetHandler {
	
	public BeansResultSetHandler(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	protected Object doHandle(ResultSet resultSet) {
		List<Object> objectList = new ArrayList<Object>();
		try{
			if (!resultSet.first()) return objectList;
			Set<Field> fieldSet = new HashSet<Field>();
			for (Field field : clazz.getDeclaredFields()) {
				if (!field.isAnnotationPresent(Column.class)) continue;
				fieldSet.add(field);
			}
			Field[] fields = fieldSet.toArray(new Field[0]);
			handle(resultSet, objectList, fields);
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
		return objectList;
	}
	
	private void handle(ResultSet resultSet, List<Object> objectList, Field[] fields) throws Exception {
		Object object = clazz.newInstance();
		for (Field field : fields) {
			String columnLabel = field.getAnnotation(Column.class).name();
			Object columnValue = resultSet.getObject(columnLabel);
			if (null == columnValue) continue;
			ReflectUtils.setValueByFieldName(object, field.getName(), columnValue);
		}
		objectList.add(object);
		if (resultSet.next()) handle(resultSet, objectList, fields);
	}
}
