package org.cisiondata.modules.consumer.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EUREKA-CLIENT")
public interface FeignConsumerClient {

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public int add(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b);
	
	@RequestMapping(value = "/minus", method = RequestMethod.GET)
	public int minus(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b);
	
}
