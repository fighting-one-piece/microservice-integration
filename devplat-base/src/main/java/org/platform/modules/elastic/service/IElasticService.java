package org.platform.modules.elastic.service;

import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-gateway", fallback = ElasticServiceFallback.class)
public interface IElasticService {

	@RequestMapping(value = "/api/v1/elastic-2.4.1/elastic/search", method = RequestMethod.GET)
	public WebResult search(@RequestParam(value = "i") String i, @RequestParam(value = "t") String t, 
		@RequestParam(value = "f") String f, @RequestParam(value = "q") String q, @RequestParam(value = "hl") 
			Integer hl, @RequestParam(value = "pn") Integer pn, @RequestParam(value = "rn") Integer rn);
	
}

class ElasticServiceFallback implements IElasticService {

	@Override
	public WebResult search(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		WebResult webResult = new WebResult();
		webResult.setCode(ResultCode.NOT_FOUNT_DATA.getCode());
		webResult.setFailure(ResultCode.NOT_FOUNT_DATA.getDesc());
		return webResult;
	}
	
	
}
