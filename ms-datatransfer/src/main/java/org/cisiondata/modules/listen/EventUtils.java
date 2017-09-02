package org.cisiondata.modules.listen;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

public class EventUtils {
	
	private static Map<String, EventInstance> map = new HashMap<String, EventInstance>();
	
	private EventUtils(){}
	
	private static class EventUtilsHolder {
    	private static final EventUtils INSTANCE = new EventUtils();
    }
    
    public static EventUtils getInstance() {
    	return EventUtilsHolder.INSTANCE;
    }
    
    public EventInstance get(String key) {
    	EventInstance eventInstance = map.get(key);
    	if (null == eventInstance) {
    		eventInstance = new EventInstance();
    		map.put(key, eventInstance);
    	}
    	return eventInstance;
    }
	
	static class EventInstance {
		
		private EventBus eventBus = null;
	    private AsyncEventBus asyncEventBus = null;
	    
	    public EventInstance() {
	    	this.eventBus = new EventBus();
	    	this.asyncEventBus = new AsyncEventBus(Executors.newFixedThreadPool(20));
	    }
	    
	    public EventBus getEventBus() {
			return eventBus;
		}

		public AsyncEventBus getAsyncEventBus() {
			return asyncEventBus;
		}
		
		public void register(Object object) {
			this.eventBus.register(object);
		}
		
		public void registerAsync(Object object) {
			this.asyncEventBus.register(object);
		}
		
		public void unregister(Object object) {
			this.eventBus.unregister(object);
		}
		
		public void unregisterAsync(Object object) {
			this.asyncEventBus.unregister(object);
		}
	}

}
