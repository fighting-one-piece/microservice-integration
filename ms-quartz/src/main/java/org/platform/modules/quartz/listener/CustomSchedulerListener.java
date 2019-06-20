package org.platform.modules.quartz.listener;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSchedulerListener implements SchedulerListener {
	
	private Logger LOG = LoggerFactory.getLogger(CustomSchedulerListener.class);

	@Override
	public void jobScheduled(Trigger trigger) {
		LOG.info("scheduler listener job scheduled {} {}", trigger.getKey().getGroup(), trigger.getKey().getName());
	}

	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		
	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		
	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		
	}

	@Override
	public void triggersPaused(String triggerGroup) {
		
	}

	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		
	}

	@Override
	public void triggersResumed(String triggerGroup) {
		
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		LOG.info("scheduler listener job added {} {} {}", jobDetail.getKey().getGroup(), 
			jobDetail.getKey().getName(), jobDetail.getJobClass());
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		LOG.info("scheduler listener job deleted {} {}", jobKey.getGroup(), jobKey.getName());
	}

	@Override
	public void jobPaused(JobKey jobKey) {
		
	}

	@Override
	public void jobsPaused(String jobGroup) {
		
	}

	@Override
	public void jobResumed(JobKey jobKey) {
		
	}

	@Override
	public void jobsResumed(String jobGroup) {
		
	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		LOG.error("scheduler error {}", msg);
	}

	@Override
	public void schedulerInStandbyMode() {
		
	}

	@Override
	public void schedulerStarted() {
		
	}

	@Override
	public void schedulerStarting() {
		
	}

	@Override
	public void schedulerShutdown() {
		
	}

	@Override
	public void schedulerShuttingdown() {
		
	}

	@Override
	public void schedulingDataCleared() {
		
	}

}
