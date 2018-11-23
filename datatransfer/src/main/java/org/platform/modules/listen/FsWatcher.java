package org.platform.modules.listen;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

public class FsWatcher {
	
	private Logger LOG = LoggerFactory.getLogger(FsWatcher.class);

	private Path path = null;
	private EventBus eventBus = null;	
	private WatchService watchService = null;
	private volatile boolean keepWatching = true;

	public FsWatcher(Path path, EventBus eventBus) {
		this.path = Objects.requireNonNull(path);
		this.eventBus = Objects.requireNonNull(eventBus);
	}

	public void start() {
		initWatchService();
		registerWatchPath();
		startWatchService();
	}

	public void stop() {
		try {
			this.watchService.close();
			this.keepWatching = false;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private WatchService initWatchService() {
		try {
			if (null == watchService) {
				watchService = FileSystems.getDefault().newWatchService();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return watchService;
	}
	
	private void registerWatchPath() {
		try {
			Files.walkFileTree(path, new WatchServiceRegisteringVisitor());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void startWatchService() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (keepWatching) {
						WatchKey watchKey = watchService.poll(10, TimeUnit.SECONDS);
						if (null == watchKey) continue;
						List<WatchEvent<?>> events = watchKey.pollEvents();
						for (WatchEvent<?> event : events) {
							String fpath = path.toString() + File.separator + ((Path) event.context()).toString();
							PathEvent pathEvent = new PathEvent(Paths.get(fpath), event.kind());
							eventBus.post(pathEvent);
						}
						watchKey.reset();
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					stop();
				}
			}
		}).start();
	}

	private class WatchServiceRegisteringVisitor extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
			return FileVisitResult.CONTINUE;
		}
	}
}
