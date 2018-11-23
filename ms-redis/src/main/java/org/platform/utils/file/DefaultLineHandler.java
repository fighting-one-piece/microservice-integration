package org.platform.utils.file;

public class DefaultLineHandler implements LineHandler {

	@SuppressWarnings("unchecked")
	@Override
	public String handle(String line) {
		return line;
	}

	@Override
	public boolean filter(String line) {
		return false;
	}

}
