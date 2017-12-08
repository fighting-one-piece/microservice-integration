package org.cisiondata.modules.scheduler.service;

import org.cisiondata.utils.exception.BusinessException;

public interface ISchedulerService {
	
	/**
	 * 启动调度器
	 * @param topic
	 * @throws BusinessException
	 */
	public void startupScheduler(String topic) throws BusinessException;

	/**
	 * 关闭调度器
	 * @param topic
	 * @throws BusinessException
	 */
	public void shutdownScheduler(String topic) throws BusinessException;
	
}
