package org.cisiondata.modules.redis.controller;

import org.cisiondata.utils.redis.RedisClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
	
	private Logger LOG = LoggerFactory.getLogger(RedisController.class);
	
	@RequestMapping(value = "/set", method = RequestMethod.POST)
	public int set(String key, Object value) {
		try {
			RedisClusterUtils.getInstance().set(key, value);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
		return 1;
	}
	
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Object get(String key) {
		try {
			return RedisClusterUtils.getInstance().get(key);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
}
