package org.platform.modules.stripe.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.platform.modules.stripe.service.IStripeService;
import org.platform.utils.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Source;

@Service("stripeService")
public class StripeServiceImpl implements IStripeService {
	
	private Logger LOG = LoggerFactory.getLogger(StripeServiceImpl.class);

	@Override
	public Map<String, Object> insertSource(Map<String, String> params) throws BusinessException {
		Map<String, Object> sourceParams = new HashMap<String, Object>();
		sourceParams.put("type", "alipay");
		sourceParams.put("currency", "usd");
		sourceParams.put("amount", 1.0d);
		sourceParams.put("redirect_return_url", "");
		try {
			Source source = Source.create(sourceParams);
			LOG.info("{}", source);
		} catch (StripeException e) {
			LOG.error(e.getStripeError().getMessage(), e);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		return result;
	}

	@Override
	public Object insertNotify(Map<String, String> params) throws BusinessException {
		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("currency", "usd");
		chargeParams.put("amount", 1.0d);
		chargeParams.put("source", "");
		try {
			Charge charge = Charge.create(chargeParams);
			charge.getLastResponse();
		} catch (StripeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
