package org.platform.modules.abstr.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.platform.modules.abstr.entity.Query;
import org.springframework.dao.DataAccessException;

public interface GenericDAO <Entity extends Serializable, PK extends Serializable> {

	/**
	 * 插入实体对象 
	 * @param entity
	 * @throws DataAccessException
	 */
	public void insert(Entity entity) throws DataAccessException;
	
	/**
	 * 批量插入实体对象
	 * @param entities
	 * @throws DataAccessException
	 */
	public void insertBatch(List<Entity> entities) throws DataAccessException;

	/**
	 * 更新实体对象
	 * @param entity
	 * @throws DataAccessException
	 */
	public void update(Entity entity) throws DataAccessException;
	
	/**
	 * 批量更新实体对象
	 * @param entities
	 * @throws DataAccessException
	 */
	public void updateBatch(List<Entity> entities) throws DataAccessException;

	/**
	 * 删除实体对象
	 * @param entity
	 * @throws DataAccessException
	 */
	public void delete(Entity entity) throws DataAccessException;

	/**
	 * 根据主键删除
	 * @param pk
	 * @throws DataAccessException
	 */
	public void deleteByPK(PK pk) throws DataAccessException;

	/**
	 * 根据主键读取实体对象
	 * @param pk
	 * @return
	 * @throws DataAccessException
	 */
	public Entity readDataByPK(PK pk) throws DataAccessException;

	/**
	 * 根据条件读取实体对象
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public Entity readDataByCondition(Query query) throws DataAccessException;
	
	/**
	 * 根据条件读取实体对象
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public Entity readDataByCondition(Map<String, Object> condition) throws DataAccessException;
	
	/**
	 * 根据条件读取实体对象列表
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public List<Entity> readDataListByCondition(Query query) throws DataAccessException;
	
	/**
	 * 根据条件读取实体对象列表
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public List<Entity> readDataListByCondition(Map<String, Object> condition) throws DataAccessException;
	
	/**
	 * 根据Query读取实体对象分页列表
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public List<Entity> readDataPaginationByQuery(Query query) throws DataAccessException;

	/**
	 * 根据条件读取实体对象分页列表
	 * @param condition
	 * @return
	 * @throws DataAccessException
	 */
	public List<Entity> readDataPaginationByCondition(Map<String, Object> condition) throws DataAccessException;
	
	/**
	 * 根据条件读取数据数量
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public Long readCountByCondition(Query query) throws DataAccessException;

	/**
	 * 刷新
	 * @throws DataAccessException
	 */
	public void flush() throws DataAccessException;
	
}
