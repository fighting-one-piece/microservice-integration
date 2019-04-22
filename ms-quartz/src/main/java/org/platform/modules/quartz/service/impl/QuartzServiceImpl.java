package org.platform.modules.quartz.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.quartz.entity.CJob;
import org.platform.modules.quartz.entity.CTrigger;
import org.platform.modules.quartz.service.IQuartzService;
import org.platform.utils.exception.BusinessException;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("quartzService")
public class QuartzServiceImpl implements IQuartzService {
	
	private static final Logger LOG = LoggerFactory.getLogger(QuartzServiceImpl.class);
	
	@Resource(name = "schedulerFactoryExtBean")
	private Scheduler scheduler = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void insert(String jobGroup, String jobName, String jobClass, String triggerGroup,
			String triggerName, String cron) throws BusinessException {
		LOG.info("Insert Scheduler {} - {} - {} - {} - {} - {} ", jobGroup, jobName, jobClass, 
				triggerGroup, triggerName, cron);
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称", jobClass, "任务实现类", cron, "任务Cron表达式");
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);
			if (scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务已经存在");
			} 
			if (StringUtils.isBlank(triggerGroup)) triggerGroup = jobGroup;
			if (StringUtils.isBlank(triggerName)) triggerName = jobName;
			TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
			if (scheduler.checkExists(triggerKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务Trigger已经存在");
			}
			CronExpression cronExpression = new CronExpression(cron);
			ScheduleBuilder<CronTrigger> cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(cronScheduleBuilder).build();
			Class<? extends Job> jobClazz = (Class<? extends Job>) Class.forName(jobClass);
			JobDetail jobDetail = JobBuilder.newJob().withIdentity(jobKey).ofType(jobClazz)
					.storeDurably(true).requestRecovery(true).build();
			scheduler.scheduleJob(jobDetail, cronTrigger);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void insert(String jobGroup, String jobName, String jobClass, Map<?, ?> jobData, String triggerGroup,
			String triggerName, String cron) throws BusinessException {
		LOG.info("Insert Scheduler {} - {} - {} - {} - {} - {} - {}", jobGroup, jobName, jobClass, 
				jobData, triggerGroup, triggerName, cron);
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称", jobClass, "任务实现类", cron, "任务Cron表达式");
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);
			if (scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务已经存在");
			} 
			if (StringUtils.isBlank(triggerGroup)) triggerGroup = jobGroup;
			if (StringUtils.isBlank(triggerName)) triggerName = jobName;
			TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
			if (scheduler.checkExists(triggerKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务Trigger已经存在");
			}
			CronExpression cronExpression = new CronExpression(cron);
			ScheduleBuilder<CronTrigger> cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(cronScheduleBuilder).build();
			Class<? extends Job> jobClazz = (Class<? extends Job>) Class.forName(jobClass);
			JobDetail jobDetail = JobBuilder.newJob().withIdentity(jobKey).ofType(jobClazz)
					.usingJobData(new JobDataMap(jobData)).storeDurably(true).requestRecovery(true).build();
			scheduler.scheduleJob(jobDetail, cronTrigger);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateJobCron(String jobGroup, String jobName, String cron) throws BusinessException {
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称", cron, "任务Cron表达式");
		try {
			JobKey jobKey = new JobKey(jobName, jobGroup);
			if (!scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务不存在");
			} 
			List<CronTrigger> cronTriggers = (List<CronTrigger>) scheduler.getTriggersOfJob(jobKey);
			if (null == cronTriggers || cronTriggers.size() == 0) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务不存在Trigger");
			}
			CronTrigger oldCronTrigger = cronTriggers.get(0);
			CronExpression cronExpression = new CronExpression(cron);
			ScheduleBuilder<CronTrigger> cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			CronTrigger newCronTrigger = oldCronTrigger.getTriggerBuilder().withSchedule(cronScheduleBuilder).build();
			scheduler.rescheduleJob(oldCronTrigger.getKey(), newCronTrigger);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	@Override
	public void updateTrigger(String triggerGroup, String triggerName, String cron) throws BusinessException {
		checkParamNotNull(triggerGroup, "Trigger组名", triggerName, "Trigger名称", cron, "Cron表达式");
		try {
			TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
			Trigger trigger = scheduler.getTrigger(triggerKey);
			if (null == trigger) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该Trigger不存在");
			}
			CronTrigger oldCronTrigger = (CronTrigger) trigger;
			CronExpression cronExpression = new CronExpression(cron);
			ScheduleBuilder<CronTrigger> cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
			CronTrigger newCronTrigger = oldCronTrigger.getTriggerBuilder().withSchedule(cronScheduleBuilder).build();
			scheduler.rescheduleJob(triggerKey, newCronTrigger);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	@Override
	public void pauseJob(String jobGroup, String jobName) throws BusinessException {
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称");
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			if (!scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务不存在");
			}
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void pauseTrigger(String triggerGroup, String triggerName) throws BusinessException {
		checkParamNotNull(triggerGroup, "Trigger组名", triggerName, "Trigger名称");
		try {
			TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
			if (!scheduler.checkExists(triggerKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该Trigger不存在");
			}
			scheduler.pauseTrigger(triggerKey);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void resumeJob(String jobGroup, String jobName) throws BusinessException {
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称");
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			if (!scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务不存在");
			}
			scheduler.resumeJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void resumeTrigger(String triggerGroup, String triggerName) throws BusinessException {
		checkParamNotNull(triggerGroup, "Trigger组名", triggerName, "Trigger名称");
		try {
			TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);
			if (!scheduler.checkExists(triggerKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该Trigger不存在");
			}
			scheduler.resumeTrigger(triggerKey);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void delete(String jobGroup, String jobName) throws BusinessException {
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称");
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			if (!scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务不存在");
			}
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public CJob readJob(String jobGroup, String jobName) throws BusinessException {
		checkParamNotNull(jobGroup, "任务组名", jobName, "任务名称");
		try {
			JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
			if (!scheduler.checkExists(jobKey)) {
				throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该任务不存在");
			}
			return readJobDTOByJobKey(jobKey);
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	public List<CJob> readJobs() throws BusinessException {
		List<CJob> jobDTOList = new ArrayList<CJob>();
		try {
			GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
			Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
			if (null == jobKeys || jobKeys.size() == 0) return jobDTOList;
			for (JobKey jobKey : jobKeys) {
				CJob jobDTO = readJobDTOByJobKey(jobKey);
				jobDTOList.add(jobDTO);
			}
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
		return jobDTOList;
	}
	
	private void checkParamNotNull(Object... params) {
		for (int i = 0, len = params.length; i < len; i++) {
			checkSingleParamNotNull(params[i], (String) params[++i]);
		}
	}
	
	private void checkSingleParamNotNull(Object paramValue, String paramName) {
		boolean isNull = false;
		if (paramValue instanceof String) {
			isNull = StringUtils.isBlank((String) paramValue);
		} else {
			isNull = null == paramValue ? true : false;
		}
		if (isNull) throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), paramName + "不能为空");
	}
	
	@SuppressWarnings("unchecked")
	private CJob readJobDTOByJobKey(JobKey jobKey) {
		CJob jobDTO = new CJob();
		try {
			jobDTO.setGroup(jobKey.getGroup());
			jobDTO.setName(jobKey.getName());
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			jobDTO.setDescription(jobDetail.getDescription());
			jobDTO.setJobClass(jobDetail.getJobClass().getName());
			jobDTO.setDurable(jobDetail.isDurable());
			jobDTO.setRequestsRecovery(jobDetail.requestsRecovery());
			List<CronTrigger> triggerList = (List<CronTrigger>) scheduler.getTriggersOfJob(jobKey);
			for (int i = 0, len = triggerList.size(); i < len; i++) {
				CronTrigger cronTrigger = triggerList.get(i);
				TriggerKey triggerKey = cronTrigger.getKey();
				CTrigger triggerDTO = new CTrigger();
				triggerDTO.setGroup(triggerKey.getGroup());
				triggerDTO.setName(triggerKey.getName());
				triggerDTO.setDescription(cronTrigger.getDescription());
				triggerDTO.setCronExpression(cronTrigger.getCronExpression());
				triggerDTO.setStartTime(cronTrigger.getStartTime());
				triggerDTO.setEndTime(cronTrigger.getEndTime());
				triggerDTO.setFinalFireTime(cronTrigger.getFinalFireTime());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
				triggerDTO.setStatus(triggerState.name());
				jobDTO.getTriggers().add(triggerDTO);
			}
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
		return jobDTO;
	}
	
}
