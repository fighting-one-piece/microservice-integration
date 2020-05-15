package org.platform.modules.abstr.service.converter;

import java.io.Serializable;
import java.util.Collection;

/** 对象转换*/
public interface IConverter<Entity1 extends Serializable, Entity2 extends Serializable> {

	/**
	 * 转换对象(双向)
	 * @param object
	 * @return
	 */
	public Object convertObject(Object object);

	/**
	 * 转换对象
	 * @param entity1
	 * @param entity2
	 */
	public void convertEntity1ToEntity2(Entity1 entity1, Entity2 entity2);

	/**
	 * 转换对象
	 * @param entity2
	 * @param entity1
	 */
	public void convertEntity2ToEntity1(Entity2 entity2, Entity1 entity1);

	/**
	 * 转换对象
	 * @param entity1Collection
	 * @param entity2Collection
	 */
	public void convertEntity1CollectionToEntity2Collection(
			Collection<Entity1> entity1Collection, Collection<Entity2> entity2Collection);

	/**
	 * 转换对象
	 * @param entity2Collection
	 * @param entity1Collection
	 */
	public void convertEntity2CollectionToEntity1Collection(
			Collection<Entity2> entity2Collection, Collection<Entity1> entity1Collection);
	
}
