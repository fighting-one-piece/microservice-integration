package org.platform.modules.wechatpay.service;

import java.util.Map;

import org.platform.utils.exception.BusinessException;

public interface IWechatpayService {
	
	/**
	 * 获取支付请求参数,包含签名
	 * @param identity 充值方式标识
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Object> mreadPaymentRequest(String identity) throws BusinessException;


	/**
	 * 验证支付异步通知回调
	 * @param callbackParams
	 * @return
	 * @throws BusinessException
	 */
	public String verifyPaymentNotifyCallback(Object callbackParams) throws BusinessException;
	
	/**
	 * 根据订单号更新订单信息
	 * @param orderNo
	 * @param isMch 是否为商户订单
	 * @return
	 * @throws BusinessException
	 */
	public void updateOrderByOrderNo(String orderNo, boolean isMch) throws BusinessException;
	

}
