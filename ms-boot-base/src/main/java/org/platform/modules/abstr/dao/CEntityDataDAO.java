package org.platform.modules.abstr.dao;

import java.util.Map;

import org.platform.modules.abstr.entity.CEntityData;
import org.platform.utils.exception.DataException;
import org.springframework.stereotype.Repository;

@Repository("centityDataDAO")
public interface CEntityDataDAO extends GenericDAO<CEntityData, Long> {

	/**
	 * 批量插入实体对象
	 * @param params
	 * @throws DataException
	 */
	public void insertBatch(Map<String, Object> params) throws DataException;
	
}
