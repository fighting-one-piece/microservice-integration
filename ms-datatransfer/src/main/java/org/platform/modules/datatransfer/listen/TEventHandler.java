package org.platform.modules.datatransfer.listen;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("teventHandler")
public class TEventHandler implements EventHandler {
	
	private Logger LOG = LoggerFactory.getLogger(TEventHandler.class);
	
	@Override
	public void handle(PathEvent event) {
		try {
			Kind<?> kind = event.getEventKind();
			if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
				File file = event.getPath().toFile();
				if (file.getName().endsWith(".xml")) {
					
				} else if (file.getName().endsWith(".ser")) {
					
				} else if (file.getName().endsWith(".json")) {
				
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
