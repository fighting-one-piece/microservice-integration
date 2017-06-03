package org.cisiondata.modules.qqrelation.service;

import java.util.List;
import java.util.Map;

import org.cisiondata.utils.exception.BusinessException;

public interface IQQGraphService {
	
	/**
	 * 插入QQ号
	 * @param nodeJSON
	 * @throws BusinessException
	 */
	public void insertQQNode(String nodeJSON) throws BusinessException;
	
	/**
	 * 批量插入QQ号
	 * @param nodes
	 * @throws BusinessException
	 */
	public void insertQQNodes(List<String> nodes) throws BusinessException;
	
	/**
	 * 插入QQ群
	 * @param nodeJSON
	 * @throws BusinessException
	 */
	public void insertQQQunNode(String nodeJSON) throws BusinessException;
	
	/**
	 * 批量插入QQ群
	 * @param nodes
	 * @throws BusinessException
	 */
	public void insertQQQunNodes(List<String> nodes) throws BusinessException;
	
	/**
	 * 插入QQ、QQ群关系
	 * @param node
	 * @throws BusinessException
	 */
	public void insertQQQunRelation(String nodeJSON) throws BusinessException;
	
	/**
	 * 批量插入QQ、QQ群关系
	 * @param nodes
	 * @throws BusinessException
	 */
	public void insertQQQunRelations(List<String> nodes) throws BusinessException;
	
	/**
	 * 根据QQ号读取QQ节点数据
	 * @param qqNum
	 * @return
	 * @throws BusinessException
	 */
	public List<Map<String, Object>> readQQNodeDataList(String qqNum) throws BusinessException;
	
	/**
	 * 根据QQ号读取QQ节点数据
	 * @param qqNum
	 * @return
	 * @throws BusinessException
	 */
	public List<Map<String, Object>> readQQNodeDataListByNickname(String nickname) throws BusinessException;
	
	/**
	 * 根据QQ群号读取QQ群节点数据
	 * @param qunNum
	 * @return
	 * @throws BusinessException
	 */
	public List<Map<String, Object>> readQunNodeDataList(String qunNum) throws BusinessException;

}
