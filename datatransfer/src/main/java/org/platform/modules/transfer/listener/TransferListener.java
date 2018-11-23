package org.platform.modules.transfer.listener;

import javax.annotation.Resource;

import org.platform.modules.listen.EventHandler;
import org.platform.modules.listen.FsWatchUtils;
import org.platform.modules.transfer.service.IConfigService;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component("transferListener")
public class TransferListener implements Listener, InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(TransferListener.class);
	
	@Resource(name = "teventHandler")
	private EventHandler eventHandler = null;
	
	@Resource(name = "configService")
	private IConfigService configService = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		startup();
	}
	
	@Override
	public boolean startup() throws BusinessException {
		try {
			String path = configService.readSystemConfigValue("transfer.file.i.path");
			LOG.info("transfer startup listen path: {}", path);
			FsWatchUtils.start(path, eventHandler);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public boolean shutdown() throws BusinessException {
		String path = configService.readSystemConfigValue("transfer.file.i.path");
		LOG.info("transfer shutdown listen path: {}", path);
		FsWatchUtils.stop(path);
		return true;
	}

}
