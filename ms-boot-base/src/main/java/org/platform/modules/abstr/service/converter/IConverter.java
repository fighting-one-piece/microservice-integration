package org.platform.modules.abstr.service.converter;

import java.io.Serializable;
import java.util.Collection;

/** 对象转换*/
public interface IConverter<Entity extends Serializable, EntityDTO extends Serializable> {

	/**
	 * 转换对象(双向)
	 * @param object
	 * @return
	 */
	public Object convertObject(Object object);

	/**
	 * 转换对象
	 * @param entity
	 * @param entityDTO
	 */
	public void convertEntity2DTO(Entity entity, EntityDTO entityDTO) ;

	/**
	 * 转换对象
	 * @param entityDTO
	 * @param entity
	 */
	public void convertDTO2Entity(EntityDTO entityDTO, Entity entity) ;

	/**
	 * 转换对象
	 * @param entityCollection
	 * @param entityDTOCollection
	 */
	public void convertEntityCollection2DTOCollection(
			Collection<Entity> entityCollection, Collection<EntityDTO> entityDTOCollection) ;

	/**
	 * 转换对象
	 * @param entityDTOCollection
	 * @param entityCollection
	 */
	public void convertDTOCollection2EntityCollection(
			Collection<EntityDTO> entityDTOCollection, Collection<Entity> entityCollection) ;
}
