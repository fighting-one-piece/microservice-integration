package org.platform.modules.alipay.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.platform.modules.alipay.service.IAlipayService;
import org.platform.modules.alipay.utils.AlipayUtils;
import org.platform.modules.bootstrap.config.AlipayConfiguration;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;

@Service("alipayWebPageService")
public class AlipayWebPageServiceImpl implements IAlipayService {

	private Logger LOG = LoggerFactory.getLogger(AlipayWebPageServiceImpl.class);
	
	@Autowired
	private AlipayClient alipayClient = null;

	@Autowired
	private AlipayConfiguration alipayConfiguration = null;
	
	@Override
	public String mreadPaymentRequest(String identity) throws BusinessException {
		double money = 0.01d;
		String outTradeNo = AlipayUtils.genOutTradeNo();
		
		//生成单据
		LOG.info("generate payment data");
		
		// 1、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        //alipayRequest.setReturnUrl(alipayConfiguration.getReturnUrl());
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(alipayConfiguration.getNotifyUrl());

        // 2、SDK已经封装掉了公共参数，这里只需要传入业务参数，请求参数查阅开头Wiki
        Map<String, String> map = new HashMap<String, String>(16);
        map.put("out_trade_no", outTradeNo);
        map.put("total_amount", String.valueOf(money));
        map.put("subject", "payment subject");
        map.put("body", "payment body");
        // 销售产品码
        map.put("product_code", "QUICK_MSECURITY_PAY");

        alipayRequest.setBizContent(GsonUtils.fromMapExtToJson(map));

        try{
            // 3、生成支付表单
            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(alipayRequest);
            if(alipayResponse.isSuccess()) {
                return alipayResponse.getBody();
            } else {
                LOG.error("【支付表单生成】失败，错误信息：{}", alipayResponse.getSubMsg());
                return "error";
            }
        } catch (Exception e) {
            LOG.error("【支付表单生成】异常，异常信息：{}", e.getMessage());
            e.printStackTrace();
        }
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String verifyPaymentNotifyCallback(Object callbackParams) throws BusinessException {
		Map<String, String[]> requestParams = (Map<String, String[]>) callbackParams;
		Map<String, String> notifyParams = new HashMap<String, String>();
		try {
			for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
				String paramKey = entry.getKey();
				notifyParams.put(paramKey, "sign".equals(paramKey) ? entry.getValue()[0] : 
					URLDecoder.decode(entry.getValue()[0], "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
		return "success";
	}
	
}
