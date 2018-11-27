package org.platform.modules.bootstrap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WechatpayConfiguration {
	
	@Value("${wechatpay.app_id}")
    private String appId = null;
    
    @Value("${wechatpay.app_secret}")
    private String appSecret = null;
	
    @Value("${wechatpay.mch_id}")
    private String mchId = null;
    
    @Value("${wechatpay.mch_secret}")
    private String mchSecret = null;
    
    @Value("${wechatpay.trade_type_app}")
    private String tradeTypeApp = null;
    
    @Value("${wechatpay.trade_type_native}")
    private String tradeTypeNative = null;
    
    @Value("${wechatpay.package}")
    private String packageWechat = null;
    
    @Value("${wechatpay.notify_url}")
    private String notifyUrl = null;

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getMchId() {
		return mchId;
	}

	public String getMchSecret() {
		return mchSecret;
	}

	public String getTradeTypeApp() {
		return tradeTypeApp;
	}

	public String getTradeTypeNative() {
		return tradeTypeNative;
	}

	public String getPackageWechat() {
		return packageWechat;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

}
