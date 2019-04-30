package org.platform.modules.elastic.controller;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.elastic.service.IElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class ElasticController {

	@Autowired
	@Qualifier("elasticService")
	private IElasticService elasticService = null;
	
	//@HystrixCommand(fallbackMethod = "doConsumeFallback")
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public Object search(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		return elasticService.search(i, t, f, q, hl, pn, rn);
	}
	
	public Object doConsumeFallback(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		return "consume fallback";
	}
	
}
