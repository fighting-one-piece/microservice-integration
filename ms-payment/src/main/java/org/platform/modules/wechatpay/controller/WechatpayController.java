package org.platform.modules.wechatpay.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.abstr.entity.Result;
import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.wechatpay.service.IWechatpayService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.image.ZXingImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class WechatpayController {
	
	private Logger LOG = LoggerFactory.getLogger(WechatpayController.class);
	
	@Resource(name = "wechatpayWebPageService")
	private IWechatpayService wechatpayWebPageService = null;
	
	@Resource(name = "wechatpayTerminalService")
	private IWechatpayService wechatpayTerminalService = null;
	
	/** 微信支付充值 */
	@RequestMapping(value = "/recharge/wechatpay/webpage", method = RequestMethod.GET)
	public void rechargeWechatpayWebPage(String identity, HttpServletResponse response) {
		try {
			ZXingImageUtils.writeToStream(300, 300, "png", String.valueOf(
				wechatpayWebPageService.mreadPaymentRequest(identity)), response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("generate verification code error {}", e.getMessage());
		}
	}
	
	/** 微信支付充值 */
	@RequestMapping(value = "/recharge/wechatpay/terminal", method = RequestMethod.POST)
	public Result rechargeWechatpayTerminal(String identity) {
		Result result = new Result();
		try {
			result.setCode(ResultCode.SUCCESS.getCode());
			result.setData("");
		} catch (BusinessException be) {
			result.setCode(ResultCode.FAILURE.getCode());
			result.setFailure(be.getDefaultMessage());
		} catch (Exception e) {
			result.setCode(ResultCode.SYSTEM_IS_BUSY.getCode());
			result.setFailure(ResultCode.SYSTEM_IS_BUSY.getDesc());
		}
		return result;
	}
	
	/** 验证微信支付充值结果 */
	@RequestMapping(value = "/recharge/wechatpay/notify", method = RequestMethod.POST)
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
