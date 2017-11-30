package org.cisiondata.modules.listen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathEvents {

	private boolean isValid;
	private Path directory = null;
	private List<PathEvent> pathEvents = new ArrayList<PathEvent>();

	public PathEvents(Path directory, boolean isValid) {
		this.directory = directory;
		this.isValid = isValid;
	}
	
	public Path getDirectory() {
		return directory;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public List<PathEvent> getPathEvents() {
		return Collections.unmodifiableList(pathEvents);
	}
	
	public void add(PathEvent pathEvent) {
		pathEvents.add(pathEvent);
	}
	
}
