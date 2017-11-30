package org.cisiondata.modules.consumer.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "api-gateway")
public interface IElasticService {

	@RequestMapping(value = "/api/v1/elastic-2.4.1/elastic/search", method = RequestMethod.GET)
	public Object search(@RequestParam(value = "i") String i, @RequestParam(value = "t") String t, 
		@RequestParam(value = "f") String f, @RequestParam(value = "q") String q, @RequestParam(value = "hl") 
			Integer hl, @RequestParam(value = "pn") Integer pn, @RequestParam(value = "rn") Integer rn);
	
}
