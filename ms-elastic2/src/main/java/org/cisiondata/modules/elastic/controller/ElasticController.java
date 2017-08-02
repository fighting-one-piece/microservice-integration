package org.cisiondata.modules.elastic.controller;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.elastic.service.IElasticV2Service;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/elastic")
public class ElasticController {

	public Logger LOG = LoggerFactory.getLogger(ElasticController.class);
	
	@Resource(name = "elasticV2Service")
	private IElasticV2Service elasticV2Service = null;
	
	@ResponseBody
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public WebResult search(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		WebResult result = new WebResult();
		try {
			result.setData(elasticV2Service.readDataList(i, t, f, q, hl, pn, rn));
			result.setCode(ResultCode.SUCCESS.getCode());
		} catch (BusinessException be) {
			result.setCode(be.getCode());
			result.setFailure(be.getMessage());
		} catch (Exception e) {
			result.setResultCode(ResultCode.FAILURE);
			result.setFailure(e.getMessage());
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/search/pagination", method = RequestMethod.GET)
	public WebResult search(String i, String t, String q, Integer hl, String s) {
		WebResult result = new WebResult();
		try {
			result.setData(elasticV2Service.readDataList(i, t, null, q, hl, s));
			result.setCode(ResultCode.SUCCESS.getCode());
		} catch (BusinessException be) {
			result.setCode(be.getCode());
			result.setFailure(be.getMessage());
		} catch (Exception e) {
			result.setResultCode(ResultCode.FAILURE);
			result.setFailure(e.getMessage());
		}
		return result;
	}
	
}
