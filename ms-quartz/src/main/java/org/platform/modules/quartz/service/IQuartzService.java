package org.platform.modules.quartz.service;

import java.util.List;
import java.util.Map;

import org.platform.modules.quartz.entity.CJob;
import org.platform.utils.exception.BusinessException;

public interface IQuartzService {

	/**
	 * 新增Job
	 * @param jobGroup
	 * @param jobName
	 * @param jobClass
	 * @param triggerGroup
	 * @param triggerName
	 * @param cron
	 * @throws BusinessException
	 */
	public void insert(String jobGroup, String jobName, String jobClass, String triggerGroup,
			String triggerName, String cron) throws BusinessException;
	
	/**
	 * 新增Job
	 * @param jobGroup
	 * @param jobName
	 * @param jobClass
	 * @param jobData
	 * @param triggerGroup
	 * @param triggerName
	 * @param cron
	 * @throws BusinessException
	 */
	public void insert(String jobGroup, String jobName, String jobClass, Map<?, ?> jobData,
			String triggerGroup, String triggerName, String cron) throws BusinessException;
	
	/**
	 * 更新Job的Cron表达式
	 * @param jobGroup
	 * @param jobName
	 * @param cron
	 * @throws BusinessException
	 */
	public void updateJobCron(String jobGroup, String jobName, String cron) throws BusinessException; 
	
	/**
	 * 更新Trigger
	 * @param triggerGroup
	 * @param triggerName
	 * @param cron
	 * @throws BusinessException
	 */
	public void updateTrigger(String triggerGroup,String triggerName, String cron) throws BusinessException;
	
	/**
	 * 暂停Job
	 * @param jobGroup
	 * @param jobName
	 * @throws BusinessException
	 */
	public void pauseJob(String jobGroup, String jobName) throws BusinessException;
	
	/**
	 * 暂停Trigger
	 * @param triggerGroup
	 * @param triggerName
	 * @throws BusinessException
	 */
	public void pauseTrigger(String triggerGroup, String triggerName) throws BusinessException;
	
	/**
	 * 恢复Job
	 * @param jobGroup
	 * @param jobName
	 * @throws BusinessException
	 */
	public void resumeJob(String jobGroup, String jobName) throws BusinessException;
	
	/**
	 * 恢复Trigger
	 * @param triggerGroup
	 * @param triggerName
	 * @throws BusinessException
	 */
	public void resumeTrigger(String triggerGroup, String triggerName) throws BusinessException;
	
	/**
	 * 删除Job
	 * @param jobGroup
	 * @param jobName
	 * @throws BusinessException
	 */
	public void delete(String jobGroup, String jobName) throws BusinessException;
	
	/**
	 * 读取Job
	 * @param jobGroup
	 * @param jobName
	 * @return
	 * @throws BusinessException
	 */
	public CJob readJob(String jobGroup, String jobName) throws BusinessException;
	
	/**
	 * 读取Job列表
	 * @return
	 * @throws BusinessException
	 */
	public List<CJob> readJobs() throws BusinessException;
	
}
