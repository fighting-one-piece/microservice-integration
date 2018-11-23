package org.platform.modules.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ConsumerController {

	@Autowired
	private RestTemplate restTemplate = null;
	
	@HystrixCommand(fallbackMethod = "doConsumeFallback")
	@RequestMapping(value = "/consume/{operation}", method = RequestMethod.GET)
	public String doConsume(@PathVariable String operation, int a, int b) {
		String url = "http://EUREKA-CLIENT:10001/" + operation + "?a=" + a + "&b=" + b;
		return restTemplate.getForEntity(url, String.class).getBody();
	}
	
	public String doConsumeFallback(String operation, int a, int b) {
		return "error";
	}
	
}
