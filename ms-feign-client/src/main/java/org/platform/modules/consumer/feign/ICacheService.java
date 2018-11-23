package org.platform.modules.consumer.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "redis-server", fallback = CacheServiceImpl.class)
public interface ICacheService {
	
	@RequestMapping(value = "/set", method = RequestMethod.POST)
	public int set(@RequestParam(value = "key") String key, @RequestParam(value = "value") Object value);

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Object get(@RequestParam(value = "key") String key);
	
}
