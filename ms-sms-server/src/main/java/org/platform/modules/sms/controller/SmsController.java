package org.platform.modules.sms.controller;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.sms.service.ISmsService;
import org.platform.utils.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/sms")
public class SmsController {

	@Autowired
	private ISmsService smsService = null;
	
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public Map<String, Object> sendMessage(String mobilePhone, String smsType, String smsTemplateCode,
			String smsTemplateParams, String smsFreeSignName) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			smsService.sendMessage(mobilePhone, smsType, smsTemplateCode, smsTemplateParams, smsFreeSignName);
			result.put("code", 1);
			result.put("data", "Send Success");
		} catch (BusinessException be) {
			result.put("code", be.getCode());
			result.put("failure", be.getMessage());
		} catch (Exception e) {
			result.put("code", 2);
			result.put("failure", e.getMessage());
		}
		return result;
	}
	
}
