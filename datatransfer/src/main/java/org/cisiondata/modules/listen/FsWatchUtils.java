package org.cisiondata.modules.listen;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.cisiondata.modules.listen.EventUtils.EventInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FsWatchUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(FsWatchUtils.class);

	private static volatile ReentrantLock lock = new ReentrantLock();

	private static Map<String, FsWatcher> fsWatchers = new HashMap<String, FsWatcher>();

	public static void start(String path, EventHandler handler) {
		FsWatcher fsWatcher = fsWatchers.get(path);
		if (null == fsWatcher) {
			lock.lock();
			if (null == fsWatcher) {
				try {
					EventInstance eventInstance = EventUtils.getInstance().get(path);
					eventInstance.registerAsync(handler);
					fsWatcher = new FsWatcher(Paths.get(path), eventInstance.getAsyncEventBus());
					fsWatcher.start();
					fsWatchers.put(path, fsWatcher);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				} finally {
					lock.unlock();
				}
			}
		}
	}
	
	public static void stop(String path) {
		FsWatcher fsWatcher = fsWatchers.get(path);
		if (null != fsWatcher) fsWatcher.stop();
		fsWatchers.remove(path);
	}
	
}
