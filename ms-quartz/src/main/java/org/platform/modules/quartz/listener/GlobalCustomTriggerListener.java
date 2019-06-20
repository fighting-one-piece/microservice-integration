package org.platform.modules.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalCustomTriggerListener implements TriggerListener {

	private Logger LOG = LoggerFactory.getLogger(GlobalCustomTriggerListener.class);
	
	@Override
	public String getName() {
		return "globalCustomTriggerListener";
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		LOG.info("trigger listener trigger fired {}", trigger.getKey().getName());
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		LOG.info("trigger listener veto job execution {}", trigger.getKey().getName());
		return false;
	}

	@Override
	public void triggerMisfired(Trigger trigger) {
		LOG.info("trigger listener trigger misfired {}", trigger.getKey().getName());
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		LOG.info("trigger listener trigger complete {}", trigger.getKey().getName());
	}

}
