package org.platform.modules.activiti.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

public interface IProcessService {
	
	/**
	 * 部署流程
	 * @param name
	 * @param category
	 * @param bpmnPath
	 * @param pngPath
	 * @throws RuntimeException
	 */
	public void deployProcess(String name, String category, String bpmnPath, String pngPath) throws RuntimeException;
	
	/**
	 * 启动流程
	 * @param processDefinitionKey
	 * @param variables
	 * @param assignee
	 * @param nextVariables
	 * @throws RuntimeException
	 */
	public void startupProcess(String processDefinitionKey, Map<String, Object> variables, String assignee,
			Map<String, Object> nextVariables) throws RuntimeException;
	
	/**
	 * 处理流程
	 * @param processInstanceId
	 * @param assignee
	 * @param taskVariables
	 * @param nextVariables
	 * @throws RuntimeException
	 */
	public void handleProcess(String processInstanceId, String assignee, Map<String, Object> taskVariables,
			Map<String, Object> nextVariables) throws RuntimeException;
	
	/**
	 * 读取任务列表
	 * @param assignee
	 * @param isCandidateUser
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws RuntimeException
	 */
	public List<Task> readTaskList(String assignee, boolean isCandidateUser, int firstResult, int maxResults) throws RuntimeException;
	
	/**
	 * 读取历史任务列表
	 * @param assignee
	 * @param isCandidateUser
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws RuntimeException
	 */
	public List<HistoricTaskInstance> readHistoryTaskList(String assignee, boolean isCandidateUser, 
			int firstResult, int maxResults) throws RuntimeException;

	/**
	 * 读取流程实例的历史任务
	 * @param processInstanceId
	 * @return
	 * @throws RuntimeException
	 */
	public List<HistoricTaskInstance> readProcessInstanceHistoryTaskList(String processInstanceId) throws RuntimeException;
	
	/**
	 * 读取流程追踪图
	 * @param processInstanceId
	 * @param output
	 * @throws RuntimeException
	 */
	public void readProcessTrackingImage(String processInstanceId, OutputStream output) throws RuntimeException;
	
}
