package org.platform.modules.cache.service.impl;

import java.util.Set;

import org.platform.modules.cache.service.ICacheService;
import org.platform.utils.redis.RedisClusterUtils;
import org.springframework.stereotype.Service;

@Service("cacheService")
public class CacheServiceImpl implements ICacheService {
	
	@Override
	public Object readKey(String key) throws RuntimeException {
		String type = RedisClusterUtils.getInstance().type(key);
		Object returnObj = null;
		switch (type) {
			case "string" : returnObj = RedisClusterUtils.getInstance().get(key); break;
			case "hash" : returnObj = RedisClusterUtils.getInstance().hgetAll(key); break;
			case "none" : break;
		}
		return returnObj;
	}

	@Override
	public Set<String> readKeys(String pattern) throws RuntimeException {
		return RedisClusterUtils.getInstance().keys(pattern);
	}
	
	@Override
	public int deleteKeys(String pattern) throws RuntimeException {
		if ("*".equals(pattern)) throw new RuntimeException("pattern must not be *");
		if (pattern.indexOf("*") == -1) pattern = "*" + pattern + "*";
		Set<String> keys = RedisClusterUtils.getInstance().keys(pattern);
		int deleteCount = 0;
		if (null != keys && keys.size() > 0) {
			for (String key : keys) {
				if (RedisClusterUtils.getInstance().delete(key) > 0) {	
					deleteCount++;
				}
			}
		}
		return deleteCount;
	}
	
}
