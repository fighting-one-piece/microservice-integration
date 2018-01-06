package org.cisiondata.modules.scheduler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Task implements Runnable {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected Map<String, Object> params = null;
	
	public Task(Map<String, Object> params) {
		this.params = params;
	}

}
