package org.platform.modules.datatransfer.listen;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;
import java.util.List;

import javax.annotation.Resource;

import org.platform.modules.datatransfer.service.IConfigService;
import org.platform.modules.datatransfer.service.IListenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("ceventHandler")
public class CEventHandler implements EventHandler {
	
	private Logger LOG = LoggerFactory.getLogger(CEventHandler.class);
	
	@Resource(name = "configService")
	private IConfigService configService = null;
	
	@Autowired
	private List<IListenService> listeners = null;

	@Override
	public void handle(PathEvent event) {
		try {
			Kind<?> kind = event.getEventKind();
			if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
				for (int i = 0, len = listeners.size(); i < len; i++) {
					listeners.get(i).shutdownListen();
				}
				configService.refreshSystemConfig();
				for (int i = 0, len = listeners.size(); i < len; i++) {
					listeners.get(i).startupListen();
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
