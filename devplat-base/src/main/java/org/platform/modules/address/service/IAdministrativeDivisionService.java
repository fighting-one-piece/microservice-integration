package org.platform.modules.address.service;

import java.util.List;

import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.address.entity.AdministrativeDivision;
import org.platform.utils.exception.BusinessException;

public interface IAdministrativeDivisionService extends IGenericService<AdministrativeDivision, Long> {

	/**
	 * 插入解析过后的行政规划数据
	 */
	public void insertAdministrativeDivisionFromParser() throws BusinessException;
	
	/**
	 * 初始化行政规划字典
	 * @throws BusinessException
	 */
	public void insertAdministrativeDivisionDictionary() throws BusinessException;
	
	/**
	 * 根据父Code读取行政规划数据列表
	 * @param parentCode
	 * @return
	 * @throws BusinessException
	 */
	public List<AdministrativeDivision> readAdministrativeDivisionsByParentCode(String parentCode) throws BusinessException;
	
	
	
}
