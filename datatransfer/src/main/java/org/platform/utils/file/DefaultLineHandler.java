package org.platform.utils.file;

public class DefaultLineHandler implements LineHandler<String> {

	@Override
	public String handle(String line) {
		return line;
	}

	@Override
	public boolean filter(String t) {
		return false;
	}

}
