package org.platform.modules.abstr.service.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.entity.QueryResult;
import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.abstr.service.converter.IConverter;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericServiceImpl<Entity extends Serializable, PK extends Serializable> 
	implements IGenericService<Entity, PK> {

	/** 日志*/
	protected Logger LOG = LoggerFactory.getLogger(getClass());

	protected Class<Entity> entityClass = null;
	
	@SuppressWarnings("unchecked")
	public GenericServiceImpl() {
		Type parameterizedType = getClass().getGenericSuperclass();
        if (!(parameterizedType instanceof ParameterizedType)) {
            parameterizedType = getClass().getSuperclass().getGenericSuperclass();
        }
        if (parameterizedType instanceof ParameterizedType) {
            entityClass = (Class<Entity>) ((ParameterizedType) parameterizedType).getActualTypeArguments()[0];
        }
	}
	
	public abstract GenericDAO<Entity, PK> obtainDAOInstance();

	protected IConverter<?,?> obtainConverter() {
		return null;
	}

	protected void preHandle(Object object) throws BusinessException {
	}
	
	protected void postHandle(Object object) throws BusinessException {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insert(Object object) throws BusinessException {
		preHandle(object);
		Entity entity = null;
		if (entityClass.isAssignableFrom(object.getClass())) {
			entity = (Entity) object;
		} else {
			entity = (Entity) obtainConverter().convertObject(object);
		}
		obtainDAOInstance().insert(entity);
		postHandle(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void update(Object object) throws BusinessException {
		preHandle(object);
		Entity entity = null;
		if (entityClass.isAssignableFrom(object.getClass())) {
			entity = (Entity) object;
		} else {
			entity = (Entity) obtainConverter().convertObject(object);
		}
		obtainDAOInstance().update(entity);
		postHandle(entity);
	}

	@Override
	public void deleteByPK(PK pk) throws BusinessException {
		obtainDAOInstance().deleteByPK(pk);
	}
	
	@Override
	public Entity readDataByPK(PK pk) throws BusinessException {
		Entity entity = obtainDAOInstance().readDataByPK(pk);
		if (null == entity) throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
		return entity;
	}

	@Override
	public Object readDataByPK(PK pk, boolean isConvert) throws BusinessException {
		Object object = obtainDAOInstance().readDataByPK(pk);
		if (null == object) throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
		return isConvert ? obtainConverter().convertObject(object) : object;
	}
	
	@Override
	public Entity readDataByCondition(Query query) throws BusinessException {
		Entity entity = obtainDAOInstance().readDataByCondition(query);
		if (null == entity) throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
		return entity;
	}

	@Override
	public Object readDataByCondition(Query query, boolean isConvert) throws BusinessException {
		Object object = obtainDAOInstance().readDataByCondition(query);
		if (null == object) throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
		return isConvert ? obtainConverter().convertObject(object) : object;
	}
	
	@Override
	public List<Entity> readDataListByCondition(Query query) throws BusinessException {
		return obtainDAOInstance().readDataListByCondition(query);
	}
	
	@Override
	public List<?> readDataListByCondition(Query query, boolean isConvert) throws BusinessException {
		List<?> dataList = obtainDAOInstance().readDataListByCondition(query);
		if (!isConvert) return dataList;
		List<Object> resultList = new ArrayList<Object>();
		for (Object object : dataList) {
			resultList.add(obtainConverter().convertObject(object));
		}
		return resultList;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public QueryResult<Entity> readDataPaginationByCondition(Query query) throws BusinessException {
		Map<String, Object> condition = query.getCondition();
		List<Entity> resultList = obtainDAOInstance().readDataPaginationByCondition(condition);
		return new QueryResult((Long) condition.get(Query.TOTAL_ROW_NUM), resultList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public QueryResult<?> readDataPaginationByCondition(Query query, boolean isConvert) throws BusinessException {
		Map<String, Object> condition = query.getCondition();
		List<?> dataList = obtainDAOInstance().readDataPaginationByCondition(condition);
		if (!isConvert) return new QueryResult((Long) condition.get(Query.TOTAL_ROW_NUM), dataList);
		List<Object> resultList = new ArrayList<Object>();
		for (int i = 0, len = dataList.size(); i < len; i++) {
			resultList.add(obtainConverter().convertObject(dataList.get(i)));
		}
		return new QueryResult((Long) condition.get(Query.TOTAL_ROW_NUM), resultList);
	}
	
}
