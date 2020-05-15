package org.platform.modules.abstr.service.converter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.platform.modules.abstr.entity.PKEntity;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConverterAbstrImpl<Entity1 extends Serializable, Entity2 extends Serializable>
	implements IConverter<Entity1, Entity2> {

	protected Logger LOG = LoggerFactory.getLogger(getClass());

	protected Class<Entity1> entity1Class = null;

	protected Class<Entity2> entity2Class = null;

	@SuppressWarnings("unchecked")
	public ConverterAbstrImpl() {
		Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
        	Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            entity1Class = (Class<Entity1>) parameterizedType[0];
            entity2Class = (Class<Entity2>) parameterizedType[1];
        }
	}

	public static IConverter<?, ?> getInstance() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convertObject(Object object) {
		Object objectTo = null;
		try {
			if (entity1Class.isAssignableFrom(object.getClass())) {
				objectTo = entity2Class.newInstance();
				convertEntity1ToEntity2((Entity1) object, (Entity2) objectTo);
			} else if (entity2Class.isAssignableFrom(object.getClass())) {
				objectTo = entity1Class.newInstance();
				convertEntity2ToEntity1((Entity2) object, (Entity1) objectTo);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return objectTo;
	}

	@Override
	public void convertEntity1ToEntity2(Entity1 entity1, Entity2 entity2) {
		if (null == entity1 || null == entity2) return;
		try {
			convert(entity1, entity2);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void convertEntity1CollectionToEntity2Collection(
			Collection<Entity1> entity1Collection, Collection<Entity2> entity2Collection) {
		if (null == entity1Collection || null == entity2Collection) return;
		Entity2 entity2 = null;
		for (Entity1 entity1 : entity1Collection) {
			try {
				entity2 = entity2Class.newInstance();
				convertEntity1ToEntity2(entity1, entity2);
				entity2Collection.add(entity2);
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void convertEntity2ToEntity1(Entity2 entity2, Entity1 entity1) {
		if (null == entity2 || null == entity1) return;
		try {
			convert(entity2, entity1);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void convertEntity2CollectionToEntity1Collection(
			Collection<Entity2> entity2Collection, Collection<Entity1> entity1Collection) {
		if (null == entity2Collection || null == entity1Collection) return;
		Entity1 entity1 = null;
		for (Entity2 entity2 : entity2Collection) {
			try {
				entity1 = entity1Class.newInstance();
				convertEntity2ToEntity1(entity2, entity1);
				entity1Collection.add(entity1);
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	protected void convert(Object objectFrom, Object objectTo) {
		Class<?> clazz = null;
		String objectFromClassName = objectFrom.getClass().getName();
		if (objectFromClassName.indexOf("$$") == -1) {
			clazz = objectFrom.getClass();
		} else {
			String className = objectFromClassName.substring(0, objectFromClassName.indexOf("$$"));
			if (className.indexOf("_") != -1) {
				className = className.substring(0, className.lastIndexOf("_"));
			}
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new BusinessException("数据转换异常: " + e.getMessage());
			}
		}
		for (Field fieldFrom : ReflectUtils.getFields(clazz)) {
			if (Modifier.isStatic(fieldFrom.getModifiers())) continue;
			String name = fieldFrom.getName();
			Class<?> type = fieldFrom.getType();
			if ("serialVersionUID".equals(name)
					|| PKEntity.class.isAssignableFrom(type)
						|| Collection.class.isAssignableFrom(type)) {
				continue;
			}
			try {
				for (Field fieldTo : objectTo.getClass().getDeclaredFields()) {
					String nameTo = fieldTo.getName();
					if (!name.equals(nameTo)) {
						continue;
					}
					LOG.debug("convert : " + objectFrom.getClass().getSimpleName() +
							"[" + name + "] to "+ objectTo.getClass().getSimpleName() +
							"[" + fieldTo.getName() + "]");
					fieldFrom.setAccessible(true);
					fieldTo.setAccessible(true);
					Object value = fieldFrom.get(objectFrom);
					fieldTo.set(objectTo, value);
					fieldTo.setAccessible(false);
					fieldFrom.setAccessible(false);
				}
			} catch (Exception e) {
				throw new BusinessException("数据转换异常: " + e.getMessage());
			}
		}
	}

}
