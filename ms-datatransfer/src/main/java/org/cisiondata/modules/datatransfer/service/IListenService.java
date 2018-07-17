package org.cisiondata.modules.datatransfer.service;

import org.cisiondata.utils.exception.BusinessException;

public interface IListenService {

	/**
	 * 启动监听
	 * @return
	 * @throws BusinessException
	 */
	public boolean startupListen() throws BusinessException;
	
	/**
	 * 关闭监听
	 * @return
	 * @throws BusinessException
	 */
	public boolean shutdownListen() throws BusinessException;
	
	/**
	 * 获取监听磁盘
	 * @return
	 * @throws BusinessException
	 */
	public String getListenDisk() throws BusinessException;
	
}
