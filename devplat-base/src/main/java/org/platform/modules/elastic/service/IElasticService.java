package org.platform.modules.elastic.service;

import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.abstr.entity.Result;
import org.platform.utils.exception.BusinessException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-gateway", qualifier = "elasticService", fallback = ElasticServiceFallback.class)
public interface IElasticService {

	@RequestMapping(value = "/elastic-2.4.1/elastic/search", method = RequestMethod.GET)
	public Result search(@RequestParam(value = "i") String i, @RequestParam(value = "t") String t, 
		@RequestParam(value = "f") String f, @RequestParam(value = "q") String q, @RequestParam(value = "hl") 
			Integer hl, @RequestParam(value = "pn") Integer pn, @RequestParam(value = "rn") Integer rn);
	
}

@Component("elasticServiceFallback")
class ElasticServiceFallback implements IElasticService {

	@Override
	public Result search(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
	}
	
	
}
