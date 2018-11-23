package org.platform.modules.sms.service;

import org.platform.utils.exception.BusinessException;

public interface ISmsService {
	
	/**
	 * 发送短信消息
	 * @param mobilePhone
	 * @param smsType
	 * @param smsTemplateCode
	 * @param smsTemplateParams
	 * @return
	 * @throws RuntimeException
	 */
	public void sendMessage(String mobilePhone, String smsType, String smsTemplateCode,
		String smsTemplateParams, String smsFreeSignName) throws BusinessException;
	
	/**
	 * 个推发送消息
	 * @param title
	 * @param text
	 * @param logo
	 * @param logoUrl
	 * @param content
	 * @param clientId
	 * @param expireTime
	 * @throws BusinessException
	 */
	public void sendMessage(String title, String text, String logo, String logoUrl, String content,
			String clientId, long expireTime) throws BusinessException;

}
