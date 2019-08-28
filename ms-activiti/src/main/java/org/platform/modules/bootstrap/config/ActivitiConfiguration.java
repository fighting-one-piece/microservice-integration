package org.platform.modules.bootstrap.config;

import javax.annotation.Resource;

import org.activiti.engine.DynamicBpmnService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class ActivitiConfiguration {

	@Resource(name = "routingDataSource")
	private AbstractRoutingDataSource routingDataSource = null;

	@Bean
	public StandaloneProcessEngineConfiguration processEngineConfiguration() {
		StandaloneProcessEngineConfiguration peConfiguration = new StandaloneProcessEngineConfiguration();
		peConfiguration.setDataSource(routingDataSource);
		peConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		peConfiguration.setAsyncExecutorActivate(false);
		return peConfiguration;
	}
	
	@Bean(name = "processEngine")
    public ProcessEngine processEngine() {
		return processEngineConfiguration().buildProcessEngine();
    }
	
	@Bean(name = "repositoryService")
    public RepositoryService repositoryService() {
		return processEngine().getRepositoryService();
    }
	
	@Bean(name = "runtimeService")
    public RuntimeService runtimeService() {
		return processEngine().getRuntimeService();
    }
	
	@Bean(name = "taskService")
    public TaskService taskService() {
		return processEngine().getTaskService();
    }
	
	@Bean(name = "historyService")
    public HistoryService historyService() {
		return processEngine().getHistoryService();
    }
	
	@Bean(name = "managementService")
    public ManagementService managementService() {
		return processEngine().getManagementService();
    }
	
	@Bean(name = "dynamicBpmnService")
    public DynamicBpmnService dynamicBpmnService() {
		return processEngine().getDynamicBpmnService();
    }

}