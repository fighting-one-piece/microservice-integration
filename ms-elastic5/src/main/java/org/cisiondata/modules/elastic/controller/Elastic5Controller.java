package org.cisiondata.modules.elastic.controller;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.elastic.service.IElastic5Service;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/elastic5")
public class Elastic5Controller {

	public Logger LOG = LoggerFactory.getLogger(Elastic5Controller.class);
	
	@Resource(name = "elastic5Service")
	private IElastic5Service elastic5Service = null;
	
	@ResponseBody
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public WebResult search(String i, String t, String q, int hl, Integer pn, Integer rn) {
		WebResult result = new WebResult();
		try {
			result.setData(elastic5Service.readDataList(i, t, q, hl, pn, rn));
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
