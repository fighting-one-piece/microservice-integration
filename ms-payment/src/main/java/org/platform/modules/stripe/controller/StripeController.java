package org.platform.modules.stripe.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.stripe.service.IStripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ApiV1RestController
public class StripeController {
	
	private Logger LOG = LoggerFactory.getLogger(StripeController.class);
	
	@Resource(name = "stripeService")
	private IStripeService stripeService = null;
	
	/**  */
	@RequestMapping(value = "/recharge/stripe/source", method = RequestMethod.GET)
	public Object rechargeStripeSource(HttpServletRequest request) {
		LOG.info("stripe source invoked!");
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> paramMap = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			params.put(entry.getKey(), entry.getValue()[0]);
		}
		return stripeService.insertSource(params);
	}

	/**  */
	@RequestMapping(value = "/recharge/stripe/notify", method = RequestMethod.POST)
	public Object verifyStripeNotify(HttpServletRequest request) {
		LOG.info("stripe notify invoked!");
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> paramMap = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			params.put(entry.getKey(), entry.getValue()[0]);
		}
		return stripeService.insertNotify(params);
	}

}
