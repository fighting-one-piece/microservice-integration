package org.platform.modules.scheduler;

import java.util.Map;

import org.platform.utils.redis.RedisClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Task implements Runnable {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected Map<String, Object> params = null;
	
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	public void put(String qname, Object object) {
		RedisClusterUtils.getInstance().listPush(qname, object);
	}
	
	public Object take(String qname) {
		return RedisClusterUtils.getInstance().listPop(qname);
	}
	
}
