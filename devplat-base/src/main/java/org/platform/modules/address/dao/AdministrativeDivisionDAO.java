package org.platform.modules.address.dao;

import java.util.List;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.address.entity.AdministrativeDivision;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository("administrativeDivisionDAO")
public interface AdministrativeDivisionDAO extends GenericDAO<AdministrativeDivision, Long> {

	/**
	 * 批量插入行政规划
	 * @param administrativeDivisions
	 * @throws DataAccessException
	 */
	public void insertBatch(List<AdministrativeDivision> administrativeDivisions) throws DataAccessException;
	
}
