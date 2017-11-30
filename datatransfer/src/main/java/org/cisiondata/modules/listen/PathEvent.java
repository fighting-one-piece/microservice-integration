package org.cisiondata.modules.listen;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class PathEvent {
	
	private Path path = null;
	
	private WatchEvent.Kind<?> eventKind = null;
	
	public PathEvent(Path path, WatchEvent.Kind<?> eventKind) {
		this.path = path;
		this.eventKind = eventKind;
	}
    
	public Path getPath() {
		return path;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public WatchEvent.Kind<?> getEventKind() {
		return eventKind;
	}
	
	public void setEventKind(WatchEvent.Kind<?> eventKind) {
		this.eventKind = eventKind;
	}

}
