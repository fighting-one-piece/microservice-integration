package org.platform.modules.activiti.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.platform.modules.activiti.service.IProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("processService")
public class ProcessServiceImpl implements IProcessService {
	
	private Logger LOG = LoggerFactory.getLogger(ProcessServiceImpl.class);

	@Resource(name = "repositoryService")
	private RepositoryService repositoryService = null;

	@Resource(name = "runtimeService")
	private RuntimeService runtimeService = null;
	
	@Resource(name = "taskService")
	private TaskService taskService = null;
	
	@Resource(name = "historyService")
	private HistoryService historyService = null;
	
	@Override
	public void deployProcess(String name, String category, String bpmnPath, String pngPath) throws RuntimeException {
		Deployment deployment = repositoryService.createDeployment()
			.addClasspathResource(bpmnPath).addClasspathResource(pngPath)
			.name(name).category(category).deploy();
		LOG.info("deploy process id {} name {}", deployment.getId(), deployment.getName());
	}
	
	@Override
	public void startupProcess(String processDefinitionKey, Map<String, Object> variables, String assignee,
			Map<String, Object> nextVariables) throws RuntimeException {
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);  
	    List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId())
	    	.orderByTaskCreateTime().desc().taskAssignee(assignee).list();
	    if (null == taskList || taskList.isEmpty()) return;
	    Task task = taskList.get(0);
	    taskService.complete(task.getId(), nextVariables);
	}
	
	@Override
	public void handleProcess(String processInstanceId, String assignee, Map<String, Object> taskVariables,
			Map<String, Object> nextVariables) throws RuntimeException {
		List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(assignee).list();
		String taskId = taskList.get(0).getId();
		taskService.setVariables(taskId, taskVariables);
	    taskService.complete(taskId, nextVariables);
	}
	
	@Override
	public List<Task> readTaskList(String assignee, boolean isCandidateUser, int firstResult, int maxResults) throws RuntimeException {
		List<Task> taskList = null;
		if (!isCandidateUser) {
			taskList = taskService.createTaskQuery().orderByTaskCreateTime().desc()
				.taskAssignee(assignee).listPage(firstResult, maxResults);
		} else {
			taskList = taskService.createTaskQuery().orderByTaskCreateTime().desc()
				.taskCandidateUser(assignee).listPage(firstResult, maxResults);
		}
		return taskList;
	}
	
	@Override
	public List<HistoricTaskInstance> readHistoryTaskList(String assignee, boolean isCandidateUser, 
			int firstResult, int maxResults) throws RuntimeException {
		List<HistoricTaskInstance> taskInstanceList = null;
		if (!isCandidateUser) {
			taskInstanceList = historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc()
				.taskAssignee(assignee).listPage(firstResult, maxResults);
		} else {
			taskInstanceList = historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc()
				.taskCandidateUser(assignee).listPage(firstResult, maxResults);
		}
		return taskInstanceList;
	}
	
	@Override
	public List<HistoricTaskInstance> readProcessInstanceHistoryTaskList(String processInstanceId) throws RuntimeException {
		return historyService.createHistoricTaskInstanceQuery()
			.processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
	}
	
	public void readProcessInstanceState1(String processInstanceId){
	    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
	        .processInstanceId(processInstanceId).singleResult();
	    if (processInstance != null) {
	    	LOG.info("{} 流程执行 {}", processInstance.getName(), processInstance.getActivityId());
	    } else {
	    	LOG.info("流程结束");
	    }
	}
	
	public void readProcessInstanceState2(String processInstanceId){
		HistoricActivityInstance hai = historyService.createHistoricActivityInstanceQuery()
		    .processInstanceId("2501").unfinished().singleResult();
		if (hai!=null) {
			LOG.info("{} 流程执行 {} {}", hai.getProcessInstanceId(), hai.getActivityName(), hai.getAssignee());
		} else {
			LOG.info("流程结束");
		}
	}
}
