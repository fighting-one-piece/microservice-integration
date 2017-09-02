package org.cisiondata.modules.listen;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

public interface EventHandler {

	@Subscribe
	@AllowConcurrentEvents
	public void handle(PathEvent event);
	
}
