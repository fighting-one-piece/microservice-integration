package org.cisiondata.modules.sms.utils;

import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;

public class SmsClient {

	// 发送短信正式URL
	private static final String NORMAL_URL = "https://eco.taobao.com/router/rest";
	// APP KEY
	private static final String APP_KEY = "XXXXXXXX";
	// APP SECRET
	private static final String APP_SECRET = "XXXXXXXX";

	private TaobaoClient client = null;

	private SmsClient() {
		client = new DefaultTaobaoClient(NORMAL_URL, APP_KEY, APP_SECRET);
	}

	private static class SmsClientHolder {
		public static SmsClient INSTANCE = new SmsClient();
	}

	public static SmsClient getInstance() {
		return SmsClientHolder.INSTANCE;
	}

	public TaobaoClient getClient() {
		return client;
	}

}
