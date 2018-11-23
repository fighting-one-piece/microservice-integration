package org.platform.modules.sms.utils;

import com.gexin.rp.sdk.http.IGtPush;

public class PushClient {

	// 由IGetui管理页面生成，是您的应用与SDK通信的标识之一，每个应用都对应一个唯一的AppID
	public static final String APP_ID = "XXXXXXXX";
	// 预先分配的第三方应用对应的Key，是您的应用与SDK通信的标识之一。
	public static final String APP_KEY = "XXXXXXXX";
	//个推服务端API鉴权码，用于验证调用方合法性。在调用个推服务端API时需要提供。（请妥善保管，避免通道被盗用）。
	public static final String MASTER_SECRET = "XXXXXXXX";
	//推送线路选择
	public static final String HOST = "http://sdk.open.api.igexin.com/apiex.htm";
	//第三方客户端个推集成鉴权码，用于验证第三方合法性。在客户端集成SDK时需要提供。
	public static final String APP_SECRET = "XXXXXXXX";
	
	private IGtPush client = null;

	private PushClient() {
		client = new IGtPush(APP_KEY, MASTER_SECRET);
	}

	private static class PushClientHolder {
		private static PushClient INSTACE = new PushClient();
	}

	public static PushClient getInstace() {
		return PushClientHolder.INSTACE;
	}

	public IGtPush getClient() {
		return client;
	}

}
