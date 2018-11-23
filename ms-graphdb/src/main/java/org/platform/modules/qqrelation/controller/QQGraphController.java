package org.platform.modules.qqrelation.controller;

import javax.annotation.Resource;

import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.qqrelation.service.IQQGraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QQGraphController {
	
	private Logger LOG = LoggerFactory.getLogger(QQGraphController.class);

	@Resource(name = "qqGraphV2Service")
	private IQQGraphService qqGraphService = null;
	
	@ResponseBody
	@RequestMapping(value = "/qqs", method = RequestMethod.POST)
	public WebResult insertQQNode(String nodeJSON) {
		WebResult webResult = new WebResult();
		try {
			qqGraphService.insertQQNode(nodeJSON);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/qqs/{qqNum}", method = RequestMethod.GET)
	public WebResult readQQNode(@PathVariable("qqNum") String qqNum) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(qqGraphService.readQQNodeDataList(qqNum));
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/qqs", method = RequestMethod.GET)
	public WebResult readQQNodeByNickname(String nickname) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(qqGraphService.readQQNodeDataListByNickname(nickname));
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/quns", method = RequestMethod.POST)
	public WebResult insertQunNode(String nodeJSON) {
		WebResult webResult = new WebResult();
		try {
			qqGraphService.insertQQQunNode(nodeJSON);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/quns/{qunNum}", method = RequestMethod.GET)
	public WebResult readQunNode(@PathVariable("qunNum") String qunNum) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(qqGraphService.readQunNodeDataList(qunNum));
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/relations", method = RequestMethod.POST)
	public WebResult insertRelationNode(String nodeJSON) {
		WebResult webResult = new WebResult();
		try {
			qqGraphService.insertQQQunRelation(nodeJSON);
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
	@ResponseBody
	@RequestMapping(value = "/qqgraph/search", method = RequestMethod.GET)
	public WebResult readDataList(String keyword) {
		WebResult webResult = new WebResult();
		try {
			webResult.setData(qqGraphService.readDataList(keyword));
			webResult.setResultCode(ResultCode.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			webResult.setResultCode(ResultCode.FAILURE);
			webResult.setFailure(e.getMessage());
		}
		return webResult;
	}
	
}
