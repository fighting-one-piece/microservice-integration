package org.platform.modules.system.service;

import org.platform.utils.exception.BusinessException;

public interface IEmailService {
	
	/**
	 * 发送邮件
	 * @param subject
	 * @param text
	 * @param to
	 * @throws BusinessException
	 */
	public void send(String subject, String text, String... to) throws BusinessException;
	
	/**
	 * 发送邮件
	 * @param from
	 * @param subject
	 * @param text
	 * @param to
	 * @throws BusinessException
	 */
	public void send(String from, String subject, String text, String... to) throws BusinessException;

}
