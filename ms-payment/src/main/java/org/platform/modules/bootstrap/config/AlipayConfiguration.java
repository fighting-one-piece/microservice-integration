package org.platform.modules.bootstrap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

@Configuration
public class AlipayConfiguration {
	
	@Value("${alipay.gateway_url}")
    private String gatewayUrl = null;
	
    @Value("${alipay.app_id}")
    private String appId = null;
    
    @Value("${alipay.merchant_private_key}")
    private String merchantPrivateKey = null;
    
    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey = null;
    
    @Value("${alipay.method}")
    private String method = null;
    
    @Value("${alipay.version}")
    private String version = null;
    
    @Value("${alipay.format}")
    private String format = null;
    
    @Value("${alipay.charset}")
    private String charset = null;
    
    @Value("${alipay.sign_type}")
    private String signType = null;
    
    @Value("${alipay.timeout}")
    private String timeout = null;
    
    @Value("${alipay.product_code}")
    private String productCode = null;
    
    @Value("${alipay.seller_id}")
    private String sellerId = null;
    
    @Value("${alipay.seller_email}")
    private String sellerEmail = null;
    
    @Value("${alipay.return_url}")
    private String returnUrl = null;
    
    @Value("${alipay.notify_url}")
    private String notifyUrl = null;
    
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, merchantPrivateKey, format, charset, alipayPublicKey, signType);
    }

	public String getGatewayUrl() {
		return gatewayUrl;
	}

	public String getAppId() {
		return appId;
	}

	public String getMerchantPrivateKey() {
		return merchantPrivateKey;
	}

	public String getAlipayPublicKey() {
		return alipayPublicKey;
	}

	public String getMethod() {
		return method;
	}

	public String getVersion() {
		return version;
	}

	public String getFormat() {
		return format;
	}

	public String getCharset() {
		return charset;
	}

	public String getSignType() {
		return signType;
	}

	public String getTimeout() {
		return timeout;
	}

	public String getProductCode() {
		return productCode;
	}

	public String getSellerId() {
		return sellerId;
	}

	public String getSellerEmail() {
		return sellerEmail;
	}

	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

}
