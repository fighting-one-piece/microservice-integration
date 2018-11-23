package org.platform.modules.wechatpay.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.alipay.service.IAlipayService;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class WechatpayController {
	
	private Logger LOG = LoggerFactory.getLogger(WechatpayController.class);
	
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
	
	/** 微信支付充值 */
	@RequestMapping(value = "/recharge/wx/payment", method = RequestMethod.POST)
	public WebResult rechargeWxPayment(String identity) {
		WebResult webResult = new WebResult();
		try {
			webResult.setCode(ResultCode.SUCCESS.getCode());
			webResult.setData("");
		} catch (BusinessException be) {
			webResult.setCode(ResultCode.FAILURE.getCode());
			webResult.setFailure(be.getDefaultMessage());
		} catch (Exception e) {
			webResult.setCode(ResultCode.SYSTEM_IS_BUSY.getCode());
			webResult.setFailure(ResultCode.SYSTEM_IS_BUSY.getDesc());
		}
		return webResult;
	}
	
	/** 验证微信支付充值结果 */
	@RequestMapping(value = "/recharge/wx/payment/notify", method = RequestMethod.POST)
	public void verifyWxPaymentNotify(HttpServletRequest request, HttpServletResponse response) {
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			in = request.getInputStream();
			int bufferSize = 1024;
            if (null != in) {
                out = new ByteArrayOutputStream();
                byte[] buff = new byte[bufferSize];
                int count = -1;
                while ((count = in.read(buff, 0, bufferSize)) != -1) {
                    out.write(buff, 0, count);
                }
                buff = null;
                out.flush();
                response.getWriter().write("");
            }
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != in ) in.close();
				if (null != out) out.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

}
