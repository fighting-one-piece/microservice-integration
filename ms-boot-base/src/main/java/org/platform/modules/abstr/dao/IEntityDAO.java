package org.platform.modules.abstr.dao;

import java.io.Serializable;

import org.springframework.dao.DataAccessException;

public interface IEntityDAO<Entity extends Serializable, PK extends Serializable> extends GenericDAO<Entity, PK> {

	/**
	 * 根据主键修改删除标记
	 * @param pk
	 * @param deleteFlag
	 * @throws DataAccessException
	 */
	public void updateDeleteFlag(PK pk, boolean deleteFlag) throws DataAccessException;
	
	/**
	 * 根据主键修改属性的值
	 * @param pk
	 * @param attribute
	 * @param value
	 * @throws DataAccessException
	 */
	public void updateAttribute(PK pk, String attribute, Object value) throws DataAccessException;
	
	/**
	 * 根据主键修改整型属性增量数
	 * @param pk
	 * @param attribute
	 * @param incr
	 * @throws DataAccessException
	 */
	public void updateIntAttributeIncr(PK pk, String attribute, int incr) throws DataAccessException;
	
}
