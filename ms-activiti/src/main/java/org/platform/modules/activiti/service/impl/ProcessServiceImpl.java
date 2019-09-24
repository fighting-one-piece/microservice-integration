package org.platform.modules.activiti.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
	
	@Override
	public void readProcessTrackingImage(String processInstanceId, OutputStream output) throws RuntimeException {
		// 获取历史流程实例
		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
			.processInstanceId(processInstanceId).singleResult();
		// 获取历史流程定义
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) 
			repositoryService).getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
		// 历史活动实例
		List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
			.processInstanceId(processInstanceId).orderByHistoricActivityInstanceId().asc().list();
		// 已执行历史节点
		List<String> executedHistoricActivityIdList = new ArrayList<String>();
		historicActivityInstanceList.forEach(historicActivityInstance -> {
				LOG.info("historic activity name {}", historicActivityInstance.getActivityName());
				executedHistoricActivityIdList.add(historicActivityInstance.getActivityId());
			});
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionEntity.getId());
		// 已执行Flow
		List<String> executedFlowIdList = getExecutedFlowIdList(bpmnModel, historicActivityInstanceList);
		ProcessDiagramGenerator processDiagramGenerator = new DefaultProcessDiagramGenerator();
		InputStream input = processDiagramGenerator.generateDiagram(bpmnModel, "png", executedHistoricActivityIdList, 
			executedFlowIdList, "宋体", "宋体", "宋体", null, 1.0d);
		try {
			IOUtils.copyLarge(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<String> getExecutedFlowIdList(BpmnModel bpmnModel, List<HistoricActivityInstance> historicActivityInstanceList) {
		List<String> executedFlowIdList = new ArrayList<String>();
		for(int i = 0, len = historicActivityInstanceList.size(); i < len - 1; i++) {
			HistoricActivityInstance historicActivityInstance = historicActivityInstanceList.get(i);
			FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(historicActivityInstance.getActivityId());
			List<SequenceFlow> sequenceFlowList = flowNode.getOutgoingFlows();
			if(sequenceFlowList.size() > 1) {
				HistoricActivityInstance nextHistoricActivityInstance = historicActivityInstanceList.get(i+1);
				sequenceFlowList.forEach(sequenceFlow -> {
					if (sequenceFlow.getTargetFlowElement().getId().equals(nextHistoricActivityInstance.getActivityId())) {
						executedFlowIdList.add(sequenceFlow.getId());
					}
				});
			} else {
				executedFlowIdList.add(sequenceFlowList.get(0).getId());
			}
		}
		return executedFlowIdList;
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
