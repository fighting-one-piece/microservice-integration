package org.platform.modules.abstr.dao;

import java.util.Map;

import org.platform.modules.abstr.entity.CEntity;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.entity.QueryResult;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository("centityDAO")
public interface CEntityDAO extends GenericDAO<CEntity, Long> {
	
	/**
	 * 根据主键修改删除标记
	 * @param table
	 * @param id
	 * @param deleteFlag
	 * @throws DataAccessException
	 */
	public void updateDeleteFlag(String table, Long id, boolean deleteFlag) throws DataAccessException;
	
	/**
	 * 根据主键读取主从数据
	 * @param params
	 * @return
	 * @throws DataAccessException
	 */
	public CEntity readDataById(Map<String, Object> params) throws DataAccessException;
	
	/**
	 * 根据条件读取主从数据
	 * @param params
	 * @return
	 * @throws DataAccessException
	 */
	public CEntity readDataByCondition(Map<String, Object> params) throws DataAccessException;
	
	/**
	 * 读取带排序的数据分页信息
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public QueryResult<CEntity> readDataPaginationByConditionWithOrder(Query query) throws DataAccessException;
	
}
