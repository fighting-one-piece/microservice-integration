package org.cisiondata.modules.abstr.dao.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.cisiondata.modules.abstr.dao.GenericDAO;
import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.abstr.entity.QueryResult;
import org.springframework.dao.DataAccessException;

public class GenericDAOImpl<Entity extends Serializable, PK extends Serializable> implements GenericDAO<Entity, PK> {

	@Override
	public void insert(Entity entity) throws DataAccessException {
	}

	@Override
	public void insert(List<Entity> entities) throws DataAccessException {
	}

	@Override
	public void update(Entity entity) throws DataAccessException {
	}

	@Override
	public void update(List<Entity> entities) throws DataAccessException {
	}

	@Override
	public void delete(Entity entity) throws DataAccessException {
	}

	@Override
	public void deleteByPK(PK pk) throws DataAccessException {
	}

	@Override
	public Entity readDataByPK(PK pk) throws DataAccessException {
		return null;
	}

	@Override
	public Entity readDataByCondition(Query query) throws DataAccessException {
		return null;
	}

	@Override
	public Entity readDataByCondition(Map<String, Object> condition) throws DataAccessException {
		return null;
	}

	@Override
	public List<Entity> readDataListByCondition(Query query) throws DataAccessException {
		return null;
	}

	@Override
	public List<Entity> readDataListByCondition(Map<String, Object> condition) throws DataAccessException {
		return null;
	}

	@Override
	public QueryResult<Entity> readDataPaginationByCondition(Query query) throws DataAccessException {
		return null;
	}

	@Override
	public Long readCountByCondition(Query query) throws DataAccessException {
		return null;
	}

	@Override
	public void flush() throws DataAccessException {
	}

}
