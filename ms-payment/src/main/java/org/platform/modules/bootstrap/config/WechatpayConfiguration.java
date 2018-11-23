package org.platform.modules.bootstrap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatpayConfiguration {
	
	@Value("${alipay.gateway_url}")
    private String gatewayUrl = null;
	
    @Value("${alipay.app_id}")
    private String appId = null;
    
    @Value("${alipay.merchant_private_key}")
    private String merchantPrivateKey = null;
    
    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey = null;
    
    @Value("${alipay.format}")
    private String format = null;
    
    @Value("${alipay.charset}")
    private String charset = null;
    
    @Value("${alipay.sign_type}")
    private String signType = null;
    
    @Value("${alipay.product_code}")
    private String productCode = null;
    
    @Value("${alipay.seller_id}")
    private String sellerId = null;
    
    @Value("${alipay.return_url}")
    private String returnUrl = null;
    
    @Value("${alipay.notify_url}")
    private String notifyUrl = null;

	public String getNotifyUrl() {
		return notifyUrl;
	}

}
