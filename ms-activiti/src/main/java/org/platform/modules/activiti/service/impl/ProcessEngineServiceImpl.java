package org.platform.modules.activiti.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.platform.modules.activiti.service.IProcessEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("processEngineService")
public class ProcessEngineServiceImpl implements IProcessEngineService {
	
	private Logger LOG = LoggerFactory.getLogger(ProcessEngineServiceImpl.class);

	@Resource(name = "repositoryService")
	private RepositoryService repositoryService = null;

	@Resource(name = "runtimeService")
	private RuntimeService runtimeService = null;
	
	@Resource(name = "taskService")
	private TaskService taskService = null;
	
	public void handleSingleAssignee() {  
	    // 根据bpmn文件部署流程  
	    repositoryService.createDeployment().addClasspathResource("singleAssigneeProcess.bpmn").deploy();
	    Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("user1", "zhangsan");
	    // 启动流程定义并设置流程变量  
	    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("singleAssigneeProcess", variables);  
	    String processInstanceId = processInstance.getId();  
	    LOG.info("process instance id %s", processInstanceId);
	    List<Task> taskList = taskService.createTaskQuery().taskAssignee("zhangsan").list();
	    if (null != taskList && !taskList.isEmpty()) {
	    	for (int i = 0, len = taskList.size(); i < len; i++) {
	    		Task task = taskList.get(i);
	            LOG.info("task id %s", task.getId());  
	            LOG.info("task name %s", task.getName());  
	            LOG.info("task assignee %s", task.getAssignee());  
	            LOG.info("task create time %s", task.getCreateTime());  
	            LOG.info("task process instance id %s", task.getProcessInstanceId());  
	            LOG.info("##################################");
	    	}
        }
	    Map<String, Object> nextVariables = new HashMap<String, Object>();
	    nextVariables.put("user2", "lisi");
	    taskService.complete(taskList.get(0).getId(), nextVariables);
	    LOG.info("task finished and continue to the next step");
	}
	
	public void handleMultiAssignee() {  
	    // 根据bpmn文件部署流程  
	    repositoryService.createDeployment().addClasspathResource("multiAssigneeProcess.bpmn").deploy();
	    Map<String, Object> variables = new HashMap<String, Object>();
	    List<String> users = new ArrayList<String>();
	    users.add("zhangsan");
	    users.add("lisi");
	    users.add("wangwu");
        variables.put("users", users);
	    // 启动流程定义并设置流程变量  
	    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("multiAssigneeProcess", variables);  
	    String processInstanceId = processInstance.getId();  
	    LOG.info("process instance id %s", processInstanceId);
	    List<Task> taskList1 = taskService.createTaskQuery().taskAssignee("zhangsan").list();
	    if (null != taskList1 && !taskList1.isEmpty()) {
	    	for (int i = 0, len = taskList1.size(); i < len; i++) {
	    		Task task = taskList1.get(i);
	            LOG.info("task id %s", task.getId());  
	            LOG.info("task name %s", task.getName());  
	            LOG.info("task assignee %s", task.getAssignee());  
	            LOG.info("task create time %s", task.getCreateTime());  
	            LOG.info("task process instance id %s", task.getProcessInstanceId());  
	            LOG.info("##################################");
	    	}
        }
	    List<Task> taskList2 = taskService.createTaskQuery().taskAssignee("lisi").list();
	    if (null != taskList2 && !taskList2.isEmpty()) {
	    	for (int i = 0, len = taskList2.size(); i < len; i++) {
	    		Task task = taskList2.get(i);
	            LOG.info("task id %s", task.getId());  
	            LOG.info("task name %s", task.getName());  
	            LOG.info("task assignee %s", task.getAssignee());  
	            LOG.info("task create time %s", task.getCreateTime());  
	            LOG.info("task process instance id %s", task.getProcessInstanceId());  
	            LOG.info("##################################");
	    	}
        }
	    List<Task> taskList3 = taskService.createTaskQuery().taskAssignee("wangwu").list();
	    if (null != taskList3 && !taskList3.isEmpty()) {
	    	for (int i = 0, len = taskList3.size(); i < len; i++) {
	    		Task task = taskList3.get(i);
	            LOG.info("task id %s", task.getId());  
	            LOG.info("task name %s", task.getName());  
	            LOG.info("task assignee %s", task.getAssignee());  
	            LOG.info("task create time %s", task.getCreateTime());  
	            LOG.info("task process instance id %s", task.getProcessInstanceId());  
	            LOG.info("##################################");
	    	}
        }
	    Map<String, Object> nextVariables = new HashMap<String, Object>();
	    nextVariables.put("user2", "maliu");
	    taskService.complete(taskList1.get(0).getId(), nextVariables);
	    LOG.info("task finished and continue to the next step");
	}
	
}
