package org.platform.modules.alipay.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.alipay.service.IAlipayService;
import org.platform.modules.alipay.utils.AlipayUtils;
import org.platform.modules.bootstrap.config.AlipayConfiguration;
import org.platform.modules.recharge.entity.RechargeMode;
import org.platform.modules.recharge.entity.RechargeRecord;
import org.platform.modules.recharge.entity.TradeStatus;
import org.platform.modules.recharge.service.IRechargeModeService;
import org.platform.modules.recharge.service.IRechargeRecordService;
import org.platform.utils.date.DateFormatter;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;

@Service("alipayTerminalService")
public class AlipayTerminalServiceImpl implements IAlipayService {

	private Logger LOG = LoggerFactory.getLogger(AlipayTerminalServiceImpl.class);
	
	@Autowired
	private AlipayConfiguration alipayConfiguration = null;
	
	@Resource(name = "rechargeModeService")
	private IRechargeModeService rechargeModeService = null;

	@Resource(name = "rechargeRecordService")
	private IRechargeRecordService rechargeRecordService = null;

	@Override
	public String mreadPaymentRequest(String identity) throws BusinessException {
		RechargeMode rechargeMode = rechargeModeService.readRechargeModeByIdentity(identity);
		Map<String, String> params = new HashMap<String, String>();
		params.put("app_id", alipayConfiguration.getAppId());
		String outTradeNo = AlipayUtils.genOutTradeNo();
		params.put("biz_content", genBizContent(rechargeMode.getTotalMoney(), "subject", "body", outTradeNo));
		params.put("method", alipayConfiguration.getMethod());
		params.put("charset", alipayConfiguration.getCharset());
		params.put("version", alipayConfiguration.getVersion());
		params.put("sign_type", alipayConfiguration.getSignType());
		params.put("notify_url", alipayConfiguration.getNotifyUrl());
		params.put("timestamp", DateFormatter.TIME.get().format(new Date()));
		try {
			String sign = AlipaySignature.rsa256Sign(AlipaySignature.getSignContent(params),
				alipayConfiguration.getAppPrivateKey(), alipayConfiguration.getCharset());
			params.put("sign", sign);
		} catch (AlipayApiException e) {
			throw new BusinessException(ResultCode.ALIPAY_SIGN_EXCEPTION);
		} 
		
		insertRechargeRecord(rechargeMode, outTradeNo, params);
		
		List<Map.Entry<String, String>> rparams = new ArrayList<Map.Entry<String, String>>(params.entrySet());
		Collections.sort(rparams, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		StringBuilder sb = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : rparams) {
				sb.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), alipayConfiguration.getCharset()) + "&");
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
		String result = sb.length() > 0 ? sb.deleteCharAt(sb.length() - 1).toString() : "";
		LOG.info("ali payment request params: {}", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String verifyPaymentNotifyCallback(Object callbackParams) throws BusinessException {
		Map<String, String[]> requestParams = (Map<String, String[]>) callbackParams;
		Map<String, String> notifyParams = new HashMap<String, String>();
		try {
			for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
				String paramKey = entry.getKey();
				notifyParams.put(paramKey, "sign".equals(paramKey) ? entry.getValue()[0] : 
					URLDecoder.decode(entry.getValue()[0], alipayConfiguration.getCharset()));
			}
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
		if (!judgeParam(notifyParams, "seller_id", alipayConfiguration.getSellerId()) ||
				!judgeParam(notifyParams, "seller_email", alipayConfiguration.getSellerEmail()) ||
					!judgeParam(notifyParams, "auth_app_id", alipayConfiguration.getAppId())) {
			LOG.error("notify verify seller_id {} or seller_email {} or auth_app_id {} error",
				notifyParams.get("seller_id"), notifyParams.get("seller_email"), notifyParams.get("auth_app_id"));
			return "FALSE";
		}
		try {
			if (!AlipaySignature.rsaCheckV1(notifyParams, alipayConfiguration.getAlipayPublicKey(), 
					alipayConfiguration.getCharset(), notifyParams.get("sign_type"))) {
				LOG.error("Alipay Signature Failure!");
				return "FALSE";
			}
		} catch (AlipayApiException e) {
			LOG.error(e.getMessage(), e);
			return "FALSE";
		}
		String totalAmountTxt = notifyParams.get("total_amount");
		if (null == totalAmountTxt || "".equals(totalAmountTxt)) return "FALSE";
		String outTradeNo = notifyParams.get("out_trade_no");
		RechargeRecord chargingRecord = rechargeRecordService.readRechargeRecordByOutTradeNo(outTradeNo);
		if (null == chargingRecord || !chargingRecord.getTotalMoney().equals(
				Double.parseDouble(totalAmountTxt))) {
			LOG.error("charging record: {}", chargingRecord);
			LOG.error("{} , {}", chargingRecord.getTotalMoney(), totalAmountTxt);
			return "FALSE";
		}
		updateChargingSuccess(chargingRecord, notifyParams);
		return "success";
	}
	
	/** 阿里APP支付字符串组合 */
	private String genBizContent(Double totalMoney, String subject, String body, 
			String outTradeNo) throws BusinessException {
		String bizContent = "" + "{\"timeout_express\":\"" + alipayConfiguration.getTimeout() + "\"," 
			+ "\"seller_id\":\"\"," + "\"product_code\":\"" + alipayConfiguration.getProductCode() + "\"," 
				+ "\"total_amount\":\"" + totalMoney + "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" 
					+ body + "\"," + "\"out_trade_no\":\"" + outTradeNo + "\"}";
		return bizContent;
	}
	
	/** 保存充值记录 */
	private void insertRechargeRecord(RechargeMode rechargeMode, String outTradeNo, Map<String, String> params) {
		RechargeRecord rechargeRecord = new RechargeRecord();
		//TODO
		/** rechargeRecord.setUserId(WebUtils.getCurrentUser().getId()); **/
		rechargeRecord.setTotalMoney(rechargeMode.getTotalMoney());
		rechargeRecord.setRechargeMode(GsonUtils.builder().toJson(rechargeMode));
		rechargeRecord.setChannel("alipay");
		rechargeRecord.setOutTradeNo(outTradeNo);
		rechargeRecord.setSign(params.get("sign"));
		rechargeRecord.setRechargeNote(GsonUtils.fromMapExtToJson(params));
		rechargeRecord.setTradeStatus(TradeStatus.INITIAL.value());
		rechargeRecord.setInsertTime(new Date());
		rechargeRecordService.insert(rechargeRecord);
	}
	
	private boolean judgeParam(Map<String, String> params, String paramKey, String targetValue) {
		String paramValue = params.get(paramKey);
		return (null != paramValue && !"".equals(paramValue) && 
				paramValue.equals(targetValue)) ? true : false;
	}
	
	/** 充值成功处理 */
	private void updateChargingSuccess(RechargeRecord rechargeRecord, Map<String, String> notifyParams) {
		String dbTradeStatus = rechargeRecord.getTradeStatus();
		if (TradeStatus.SUCCESS.value().equals(dbTradeStatus) || 
			TradeStatus.FINISHED.value().equals(dbTradeStatus)) return;
		RechargeRecord uRechargeRecord = new RechargeRecord();
		uRechargeRecord.setUserId(rechargeRecord.getUserId());
		uRechargeRecord.setOutTradeNo(rechargeRecord.getOutTradeNo());
		uRechargeRecord.setTradeNo(notifyParams.get("trade_no"));
		uRechargeRecord.setResultNote(GsonUtils.fromMapExtToJson(notifyParams));
		String tradeStatus = notifyParams.get("trade_status");
		uRechargeRecord.setTradeStatus(TradeStatus.convert(tradeStatus));
		rechargeRecordService.update(uRechargeRecord); 
		/**
		RechargeMode rechargeMode = rechargeRecord.obtainRechargeMode();
		userBizService.updateDCoinByUserId(rechargeRecord.getUserId(), rechargeMode.getdCoin());
		**/
	}
	
}
