package org.platform.modules.bootstrap.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.platform.modules.quartz.factory.SchedulerFactoryExtBean;
import org.platform.modules.quartz.listener.CustomSchedulerListener;
import org.platform.modules.quartz.listener.GlobalCustomJobListener;
import org.platform.modules.quartz.listener.GlobalCustomTriggerListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfiguration {
	
	private Logger LOG = LoggerFactory.getLogger(QuartzConfiguration.class);
	
	@Value("${spring.quartz.auto-startup}")
	private boolean autoStartup = false;
	
	@Value("${spring.quartz.startup-delay}")
	private int startupDelay = 10;
	
	@Value("${spring.quartz.scheduler-name}")
	private String schedulerName = null;
	
	@Value("${spring.quartz.overwrite-existing-jobs}")
	private boolean overwriteExistingJobs = true;
	
	@Value("${spring.quartz.wait-for-jobs-to-complete-on-shutdown}")
	private boolean waitForJobsToCompleteOnShutdown = false;
	
	@Resource(name = "extAdaptableJobFactory")
    private JobFactory jobFactory = null;
	
	@Resource(name = "routingDataSource")
	private DataSource dataSource = null;
	
	@Resource(name = "transactionManager")
	private DataSourceTransactionManager transactionManager = null;
	
	@Bean("schedulerFactoryExtBean")
	public SchedulerFactoryBean schedulerFactoryExtBean() {
		SchedulerFactoryExtBean schedulerFactoryBean = new SchedulerFactoryExtBean();
		schedulerFactoryBean.setAutoStartup(autoStartup);
		schedulerFactoryBean.setStartupDelay(startupDelay);
		schedulerFactoryBean.setSchedulerName(schedulerName);
		//更新trigger的表达式时同步数据到数据库qrtz_cron_triggers表开启
		schedulerFactoryBean.setOverwriteExistingJobs(overwriteExistingJobs); 
		schedulerFactoryBean.setJobFactory(jobFactory);
		schedulerFactoryBean.setDataSource(dataSource);
		schedulerFactoryBean.setTransactionManager(transactionManager);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(waitForJobsToCompleteOnShutdown);
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext"); //注入applicationContext
        return schedulerFactoryBean;
	}	
	
	@Bean("scheduler")
	public Scheduler scheduler() {
		Scheduler scheduler = schedulerFactoryExtBean().getScheduler();
		try {
	    	ListenerManager listenerManager = scheduler.getListenerManager();
	    	listenerManager.addSchedulerListener(new CustomSchedulerListener());
	    	listenerManager.addJobListener(new GlobalCustomJobListener());
	    	listenerManager.addTriggerListener(new GlobalCustomTriggerListener());
		} catch (SchedulerException e) {
			LOG.error(e.getMessage(), e);
		}
		return scheduler;
	}
	
}

/**
@Component
class QuartzSchedulerFactoryBeanCustomizer implements SchedulerFactoryBeanCustomizer {
	
	@Resource(name = "routingDataSource")
	private DataSource dataSource = null;
	
	@Resource(name = "transactionManager")
	private DataSourceTransactionManager transactionManager = null;

	@Override
	public void customize(SchedulerFactoryBean schedulerFactoryBean) {
		schedulerFactoryBean.setAutoStartup(true);
		schedulerFactoryBean.setStartupDelay(10);
		schedulerFactoryBean.setSchedulerName("Quartz Scheduler");
		schedulerFactoryBean.setDataSource(dataSource);
		schedulerFactoryBean.setTransactionManager(transactionManager);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(false);
		schedulerFactoryBean.setOverwriteExistingJobs(true); //更新trigger的表达式时同步数据到数据库qrtz_cron_triggers表开启
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext"); //注入applicationContext
	}
	
}
*/
