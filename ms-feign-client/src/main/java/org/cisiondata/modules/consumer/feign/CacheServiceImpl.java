package org.cisiondata.modules.consumer.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class CacheServiceImpl implements ICacheService {
	
	private final Logger LOG = LoggerFactory.getLogger(CacheServiceImpl.class);

	@Override
	public int set(@RequestParam String key, @RequestParam Object value) {
		LOG.info("invoke set error ! {} - {}", key, value);
		return 0;
	}

	@Override
	public Object get(@RequestParam String key) {
		return "invoke get error!";
	}

}
