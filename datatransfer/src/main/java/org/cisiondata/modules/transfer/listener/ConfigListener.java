package org.cisiondata.modules.transfer.listener;

import java.io.File;

import javax.annotation.Resource;

import org.cisiondata.modules.listen.EventHandler;
import org.cisiondata.modules.listen.FsWatchUtils;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component("configListener")
public class ConfigListener implements InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(ConfigListener.class);
	
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
