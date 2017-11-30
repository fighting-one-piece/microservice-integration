package org.cisiondata.utils.file;

public interface LineHandler<T> {

	/**
	 * 处理行数据
	 * @param line
	 * @return
	 */
	public T handle(String line);
	
	/**
	 * 过滤行数据
	 * @param line
	 * @return
	 */
	public boolean filter(T t);
	
}
