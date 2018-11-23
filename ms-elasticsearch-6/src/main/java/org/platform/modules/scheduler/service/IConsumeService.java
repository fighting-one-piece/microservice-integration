package org.platform.modules.scheduler.service;

import java.util.List;

public interface IConsumeService {
	
	/**
	 * 处理单条消息
	 * @param message
	 * @throws RuntimeException
	 */
	public void handle(String message) throws RuntimeException;
	
	/**
	 * 处理多条消息
	 * @param messages
	 * @throws RuntimeException
	 */
	public void handle(List<String> messages) throws RuntimeException;
	
}
