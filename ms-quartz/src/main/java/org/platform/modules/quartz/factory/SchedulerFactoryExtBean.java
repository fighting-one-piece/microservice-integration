package org.platform.modules.quartz.factory;

import java.util.Map;

import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class SchedulerFactoryExtBean extends SchedulerFactoryBean {
	
	private Logger LOG = LoggerFactory.getLogger(SchedulerFactoryExtBean.class);
	
	private String schedulerContextAsMapText = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (isAutoStartup()) {
			super.afterPropertiesSet();
		}
	}
	
	@Override
	public void setSchedulerContextAsMap(Map<String, ?> schedulerContextAsMap) {
		LOG.info("scheduler context map: {}", schedulerContextAsMap);
		super.setSchedulerContextAsMap(schedulerContextAsMap);
	}
	
	@Override
	public void setTriggers(Trigger... triggers) {
		super.setTriggers(triggers);
	}
	
	public String getSchedulerContextAsMapText() {
		return schedulerContextAsMapText;
	}

	public void setSchedulerContextAsMapText(String schedulerContextAsMapText) {
		this.schedulerContextAsMapText = schedulerContextAsMapText;
	}
	
}
