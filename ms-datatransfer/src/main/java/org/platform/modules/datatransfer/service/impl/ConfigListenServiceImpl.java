package org.platform.modules.datatransfer.service.impl;

import java.io.File;

import javax.annotation.Resource;

import org.platform.modules.datatransfer.listen.EventHandler;
import org.platform.modules.datatransfer.listen.FsWatchUtils;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component("configListenService")
public class ConfigListenServiceImpl implements InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(ConfigListenServiceImpl.class);
	
	private String path = null;
	
	@Resource(name = "ceventHandler")
	private EventHandler eventHandler = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String userDir = System.getProperty("user.dir");
		this.path = userDir + File.separator + "config";
		LOG.info("config listen path: {}", path);
		startup();
	}
	
	public boolean startup() throws BusinessException {
		try {
			FsWatchUtils.start(path, eventHandler);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public boolean shutdown() throws BusinessException {
		FsWatchUtils.stop(path);
		return true;
	}

}
