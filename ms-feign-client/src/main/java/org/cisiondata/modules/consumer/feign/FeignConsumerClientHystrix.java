package org.cisiondata.modules.consumer.feign;

import org.springframework.web.bind.annotation.RequestParam;

//@Component("feignConsumerClientHystrix")
public class FeignConsumerClientHystrix implements FeignConsumerClient {

	@Override
	public int add(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b) {
		return -1;
	}

	@Override
	public int minus(@RequestParam(value = "a") int a, @RequestParam(value = "b") int b) {
		return -1;
	}
	
}
