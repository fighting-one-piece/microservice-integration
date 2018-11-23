package org.platform.modules.elastic.controller;

import org.platform.modules.elastic.service.IElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ElasticController {

	@Autowired
	private IElasticService elasticService = null;
	
	@HystrixCommand(fallbackMethod = "doConsumeFallback")
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public Object search(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		return elasticService.search(i, t, f, q, hl, pn, rn);
	}
	
	public Object doConsumeFallback(String i, String t, String f, String q, Integer hl, Integer pn, Integer rn) {
		return "consume fallback";
	}
	
}
