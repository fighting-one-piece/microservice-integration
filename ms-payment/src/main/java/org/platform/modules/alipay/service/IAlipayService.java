package org.platform.modules.alipay.service;

import org.platform.utils.exception.BusinessException;

public interface IAlipayService {

	/**
	 * 获取支付请求
	 * @param identity 充值方式标识
	 * @return
	 * @throws BusinessException
	 */
	public String mreadPaymentRequest(String identity) throws BusinessException;


	/**
	 * 验证支付异步通知回调
	 * @param callbackParams
	 * @return
	 * @throws BusinessException
	 */
	public String verifyPaymentNotifyCallback(Object callbackParams) throws BusinessException;

}
