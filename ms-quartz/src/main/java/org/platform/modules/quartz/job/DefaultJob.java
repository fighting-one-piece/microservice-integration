package org.platform.modules.quartz.job;

import java.util.Map;

import javax.annotation.Resource;

import org.platform.modules.quartz.service.IQuartzService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class DefaultJob extends QuartzJobBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultJob.class);
	
	private int injectValue1 = 0;
	
	private String injectValue2 = null;
	
	private IQuartzService quartzService = null;
	
	@Resource(name = "quartzService")
	private IQuartzService quartzInjectService = null;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
			ApplicationContext ac = (ApplicationContext) schedulerContext.get("applicationContext");
			quartzService = ac.getBean("quartzService", IQuartzService.class);
			LOG.info("resource inject service: {}", quartzInjectService);
			LOG.info("inject service: {}", quartzService);
			LOG.info("inject value 1: {}", injectValue1);
			LOG.info("inject value 2: {}", injectValue2);
			Map<String, Object> dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
			if (null != dataMap && !dataMap.isEmpty()) {
				LOG.info("datamap inject value 1: {}", dataMap.get("injectValue1"));
				LOG.info("datamap inject value 2: {}", dataMap.get("injectValue2"));
			} else {
				LOG.info("datamap is empty");
			}
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	
	public int getInjectValue1() {
		return injectValue1;
	}

	public void setInjectValue1(int injectValue1) {
		this.injectValue1 = injectValue1;
	}

	public String getInjectValue2() {
		return injectValue2;
	}

	public void setInjectValue2(String injectValue2) {
		this.injectValue2 = injectValue2;
	}

	public IQuartzService getQuartzService() {
		return quartzService;
	}

	public void setQuartzService(IQuartzService quartzService) {
		this.quartzService = quartzService;
	}

}
