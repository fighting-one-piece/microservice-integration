package org.cisiondata.modules.sms.service.impl;

import org.cisiondata.modules.sms.service.ISmsService;
import org.cisiondata.modules.sms.utils.PushClient;
import org.cisiondata.modules.sms.utils.SmsClient;
import org.cisiondata.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.taobao.api.ApiException;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

@Service("smsService")
public class SmsServiceImpl implements ISmsService {
	
	private Logger LOG = LoggerFactory.getLogger(SmsServiceImpl.class);
	
	@Override
	public void sendMessage(String mobilePhone, String smsType, String smsTemplateCode,
		String smsTemplateParams, String smsFreeSignName) throws BusinessException {
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setRecNum(mobilePhone);
		req.setSmsType(smsType);
		req.setSmsTemplateCode(smsTemplateCode);
		req.setSmsParamString(smsTemplateParams);
		req.setSmsFreeSignName(smsFreeSignName);
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = SmsClient.getInstance().getClient().execute(req);
		} catch (ApiException e) {
			throw new BusinessException(1001, "API Exception");
		}
		LOG.info("手机号码: {} 发送验证码回执信息: {}", mobilePhone, rsp.getBody());
		if (rsp.getBody().contains("error_response")) {
			throw new BusinessException(1002, "Send Exception");
		}
	}
	
	@Override
	public void sendMessage(String title, String text, String logo, String logoUrl, String content, String clientId,
			long expireTime) throws BusinessException {
		NotificationTemplate template = new NotificationTemplate();
		// 设置APPID与APPKEY
		template.setAppId(PushClient.APP_ID);
		template.setAppkey(PushClient.APP_KEY);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(1);
		template.setTransmissionContent(content);
		Style0 style = new Style0();
		// 设置通知栏标题与内容
		style.setTitle(title);
		style.setText(text);
		// 配置通知栏图标
		style.setLogo(logo);
		// 配置通知栏网络图标
		style.setLogoUrl(logoUrl);
		// 设置通知是否响铃，震动，或者可清除
		style.setRing(true);
		style.setVibrate(true);
		style.setClearable(true);
		template.setStyle(style);
		SingleMessage message = new SingleMessage();
		message.setData(template);
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(expireTime);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
		message.setPushNetWorkType(0);
		Target target = new Target();
		target.setAppId(PushClient.APP_ID);
		target.setClientId(clientId);
		//target.setAlias(Alias);
		IPushResult pushResult = null;
		IGtPush pushClient = PushClient.getInstace().getClient();
		try {
			pushResult = pushClient.pushMessageToSingle(message, target);
		} catch (RequestException e) {
			pushResult = pushClient.pushMessageToSingle(message, target, e.getRequestId());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(1001, "API Exception");
		}
		if (pushResult != null) {
			LOG.info("clientId ： {} 个推发送成功 {}", clientId, pushResult.getResponse().toString());
		} else {
			LOG.error("clientId ： {} 个推发送失败", clientId);
			throw new BusinessException(1002, "Send Exception");
		}
	}

}
