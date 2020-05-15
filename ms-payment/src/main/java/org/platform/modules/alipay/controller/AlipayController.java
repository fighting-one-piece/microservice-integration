package org.platform.modules.alipay.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.abstr.entity.Result;
import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.alipay.service.IAlipayService;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class AlipayController {
	
	private Logger LOG = LoggerFactory.getLogger(AlipayController.class);
	
	@Resource(name = "alipayWebPageService")
	private IAlipayService alipayWebPageService = null;
	
	@Resource(name = "alipayTerminalService")
	private IAlipayService alipayTerminalService = null;
	
	/** 阿里支付充值 */
	@RequestMapping(value = "/recharge/alipay/webpage", method = RequestMethod.GET)
	public void rechargeAlipayWebPage(HttpServletResponse response) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write(alipayWebPageService.mreadPaymentRequest(""));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/** 阿里支付充值 */
	@RequestMapping(value = "/recharge/alipay/terminal", method = RequestMethod.POST)
	public Result rechargeAlipayTerminal(String identity) {
		Result result = new Result();
		try {
			result.setCode(ResultCode.SUCCESS.getCode());
			result.setData(alipayTerminalService.mreadPaymentRequest(identity));
		} catch (BusinessException be) {
			result.setCode(ResultCode.FAILURE.getCode());
			result.setFailure(be.getDefaultMessage());
		} catch (Exception e) {
			result.setCode(ResultCode.SYSTEM_IS_BUSY.getCode());
			result.setFailure(ResultCode.SYSTEM_IS_BUSY.getDesc());
		}
		return result;
	}

	/** 验证阿里支付充值结果 */
	@RequestMapping(value = "/recharge/alipay/notify", method = RequestMethod.POST)
	public void verifyAlipayWebPageNotify(HttpServletRequest request, HttpServletResponse response) {
		try {
			LOG.info("payment notify invoked!");
			alipayWebPageService.verifyPaymentNotifyCallback(request.getParameterMap());
			response.getWriter().write("success");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/** 验证阿里支付充值结果 */
	@RequestMapping(value = "/recharge/alipay/return", method = RequestMethod.POST)
	public void verifyAlipayWebPageReturn(HttpServletRequest request, HttpServletResponse response) {
		try {
			LOG.info("payment return invoked!");
			alipayWebPageService.verifyPaymentNotifyCallback(request.getParameterMap());
			response.getWriter().write("success");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/** 验证阿里支付充值结果 */
	@RequestMapping(value = "/recharge/alipay/terminal/notify", method = RequestMethod.POST)
	public void verifyAlipayTerminalNotify(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.getWriter().write(alipayTerminalService.verifyPaymentNotifyCallback(request.getParameterMap()));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
