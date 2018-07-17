package org.cisiondata.modules.abstr.service.converter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.cisiondata.modules.abstr.entity.PKEntity;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConverterAbstrImpl<Entity extends Serializable, EntityDTO extends Serializable>
	implements IConverter<Entity, EntityDTO> {

	protected Logger LOG = LoggerFactory.getLogger(getClass());

	protected Class<Entity> entityClass = null;

	protected Class<EntityDTO> entityDTOClass = null;

	@SuppressWarnings("unchecked")
	public ConverterAbstrImpl() {
		Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
        	Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            entityClass = (Class<Entity>) parameterizedType[0];
            entityDTOClass = (Class<EntityDTO>) parameterizedType[1];
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
			if (entityClass.isAssignableFrom(object.getClass())) {
				objectTo = entityDTOClass.newInstance();
				convertEntity2DTO((Entity) object, (EntityDTO) objectTo);
			} else if (entityDTOClass.isAssignableFrom(object.getClass())) {
				objectTo = entityClass.newInstance();
				convertDTO2Entity((EntityDTO) object, (Entity) objectTo);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return objectTo;
	}

	@Override
	public void convertEntity2DTO(Entity entity, EntityDTO entityDTO) {
		if (null == entity || null == entityDTO) {
			return;
		}
		try {
			convert(entity, entityDTO);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void convertEntityCollection2DTOCollection(
			Collection<Entity> entityCollection, Collection<EntityDTO> entityDTOCollection) {
		if (null == entityCollection || null == entityDTOCollection) {
			return;
		}
		EntityDTO entityDTO = null;
		for (Entity entity : entityCollection) {
			try {
				entityDTO = entityDTOClass.newInstance();
				convertEntity2DTO(entity, entityDTO);
				entityDTOCollection.add(entityDTO);
			} catch (InstantiationException e) {
				LOG.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void convertDTO2Entity(EntityDTO entityDTO, Entity entity) {
		if (null == entityDTO || null == entity) {
			return;
		}
		try {
			convert(entityDTO, entity);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void convertDTOCollection2EntityCollection(
			Collection<EntityDTO> entityDTOCollection, Collection<Entity> entityCollection) {
		if (null == entityDTOCollection || null == entityCollection) {
			return;
		}
		Entity entity = null;
		for (EntityDTO entityDTO : entityDTOCollection) {
			try {
				entity = entityClass.newInstance();
				convertDTO2Entity(entityDTO, entity);
				entityCollection.add(entity);
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
