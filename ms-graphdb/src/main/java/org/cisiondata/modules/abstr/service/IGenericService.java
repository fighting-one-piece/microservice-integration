package org.cisiondata.modules.abstr.service;

import java.io.Serializable;
import java.util.List;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.utils.exception.BusinessException;

public interface IGenericService<Entity extends Serializable, PK extends Serializable> {

	/**
	 * 插入对象
	 * @param object
	 * @throws BusinessException
	 */
	public void insert(Object object) throws BusinessException;

	/**
	 * 更新对象
	 * @param object
	 * @throws BusinessException
	 */
	public void update(Object object) throws BusinessException;

	/**
	 * 删除
	 * @param pk
	 * @throws BusinessException
	 */
	public void deleteByPK(PK pk) throws BusinessException;

	/**
	 * 根据主键读取对象
	 * @param pk
	 * @param isConvert
	 * @return
	 * @throws BusinessException
	 */
	public Object readDataByPK(PK pk, boolean isConvert) throws BusinessException;

	/**
	 * 根据条件读取对象
	 * @param query
	 * @param isConvert
	 * @return
	 * @throws BusinessException
	 */
	public Object readDataByCondition(Query query, boolean isConvert) throws BusinessException;

	/**
	 * 根据条件读取对象列表
	 * @param query
	 * @param isConvert
	 * @return
	 * @throws BusinessException
	 */
	public List<?> readDataListByCondition(Query query, boolean isConvert) throws BusinessException;
	
	/**
	 * 根据条件读取对象分页列表
	 * @param query
	 * @param isConvert
	 * @return
	 * @throws BusinessException
	 */
	public QueryResult<?> readDataPaginationByCondition(Query query, boolean isConvert) throws BusinessException;

}
