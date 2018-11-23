package org.platform.modules.address.service;

import java.util.List;

import org.platform.utils.exception.BusinessException;

public interface IAddressService {

	/**
	 * 抽取地址中的行政规划
	 * @param address
	 * @return
	 * @throws BusinessException
	 */
	public List<String> readAdministrativeDivision(String address) throws BusinessException;
	
	public List<String> read3AdministrativeDivision(String address) throws BusinessException;
	
	public List<String> read5AdministrativeDivision(String address) throws BusinessException;
	
}
