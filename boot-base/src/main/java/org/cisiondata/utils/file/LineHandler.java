package org.cisiondata.utils.file;

public interface LineHandler {

	/**
	 * 处理行数据
	 * @param line
	 * @return
	 */
	public <T> T handle(String line);
	
	/**
	 * 过滤行数据
	 * @param line
	 * @return
	 */
	public boolean filter(String line);
	
}
