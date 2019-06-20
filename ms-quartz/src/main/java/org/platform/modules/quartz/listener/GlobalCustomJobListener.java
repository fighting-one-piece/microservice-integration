package org.platform.modules.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalCustomJobListener implements JobListener {
	
	private Logger LOG = LoggerFactory.getLogger(GlobalCustomJobListener.class);

	@Override
	public String getName() {
		return "globalCustomJobListener";
	}

	/** Scheduler在JobDetail将要被执行时调用这个方法 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		LOG.info("job listener job to be executed {}", context);
	}

	/** Scheduler在JobDetail即将被执行，但又被 TriggerListener否决了时调用这个方法 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		LOG.info("job listener job execution vetoed {}", context);
	}

	/** Scheduler在JobDetail被执行之后调用这个方法 */
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		LOG.info("job listener job was executed {}", context);
		LOG.error(jobException.getMessage(), jobException);
	}

}
