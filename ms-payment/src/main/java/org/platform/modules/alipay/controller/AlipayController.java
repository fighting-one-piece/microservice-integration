package org.platform.modules.alipay.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.alipay.service.IAlipayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class AlipayController {
	
	private Logger LOG = LoggerFactory.getLogger(AlipayController.class);
	
	@Resource(name = "alipayWebPageService")
	private IAlipayService alipayWebPageService = null;
	
	/** 阿里支付充值 */
	@RequestMapping(value = "/recharge/ali/payment", method = RequestMethod.GET)
	public void rechargeAliPayment(HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(alipayWebPageService.mreadPaymentRequest(""));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/** 阿里支付充值 */
	@RequestMapping(value = "/recharge/ali/payment", method = RequestMethod.POST)
	public void rechargeAliPayment(String identity, HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(alipayWebPageService.mreadPaymentRequest(identity));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/** 验证阿里支付充值结果 */
	@RequestMapping(value = "/recharge/ali/payment/notify", method = RequestMethod.POST)
	public void verifyAliPaymentNotify(HttpServletRequest request, HttpServletResponse response) {
		try {
			LOG.info("payment notify invoked!");
			alipayWebPageService.verifyPaymentNotifyCallback(request.getParameterMap());
			response.getWriter().write("success");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
