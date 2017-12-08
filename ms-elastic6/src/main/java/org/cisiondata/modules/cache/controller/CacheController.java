package org.cisiondata.modules.cache.controller;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.web.ResultCode;
import org.cisiondata.modules.abstr.web.WebResult;
import org.cisiondata.modules.cache.service.ICacheService;
import org.cisiondata.utils.exception.BusinessException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cache")
public class CacheController {
	
	@Resource(name = "cacheService")
	private ICacheService cacheService = null;
	
	@RequestMapping(value = "/k/search", method = RequestMethod.GET)
	public WebResult searchKey(String key) {
		WebResult result = new WebResult();
		try {
			result.setData(cacheService.readKey(key));
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
	

	@RequestMapping(value = "/ks/search", method = RequestMethod.GET)
	public WebResult searchKeys(String pattern) {
		WebResult result = new WebResult();
		try {
			result.setData(cacheService.readKeys(pattern));
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
	
	@RequestMapping(value = "/ks/delete", method = RequestMethod.POST)
	public WebResult delete(String pattern) {
		WebResult result = new WebResult();
		try {
			result.setData(cacheService.deleteKeys(pattern));
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
