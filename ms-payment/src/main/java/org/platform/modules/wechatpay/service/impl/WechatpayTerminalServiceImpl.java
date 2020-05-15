package org.platform.modules.wechatpay.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.http.entity.ContentType;
import org.platform.modules.abstr.entity.ResultCode;
import org.platform.modules.bootstrap.config.WechatpayConfiguration;
import org.platform.modules.recharge.entity.RechargeMode;
import org.platform.modules.recharge.entity.RechargeRecord;
import org.platform.modules.recharge.entity.TradeStatus;
import org.platform.modules.recharge.service.IRechargeModeService;
import org.platform.modules.recharge.service.IRechargeRecordService;
import org.platform.modules.wechatpay.service.IWechatpayService;
import org.platform.modules.wechatpay.utils.WechatpayUtils;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.http.HttpClientUtils;
import org.platform.utils.json.GsonUtils;
import org.platform.utils.web.IPUtils;
import org.platform.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service("wechatpayTerminalService")
public class WechatpayTerminalServiceImpl implements IWechatpayService {
	
	private Logger LOG = LoggerFactory.getLogger(WechatpayTerminalServiceImpl.class);

	private static final String ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	private static final String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	
	@Autowired
	private WechatpayConfiguration wechatpayConfiguration = null;
	
	@Resource(name = "rechargeModeService")
	private IRechargeModeService rechargeModeService = null;

	@Resource(name = "rechargeRecordService")
	private IRechargeRecordService rechargeRecordService = null;
	
	@Override
	public Object mreadPaymentRequest(String identity) throws BusinessException {
		RechargeMode rechargeMode = rechargeModeService.readRechargeModeByIdentity(identity);
		TreeMap<String, Object> params = new TreeMap<String, Object>();
		params.put("appid", wechatpayConfiguration.getAppId());
		params.put("mch_id", wechatpayConfiguration.getMchId());
		params.put("body", "");
		params.put("trade_type", wechatpayConfiguration.getTradeTypeApp());
		params.put("notify_url", wechatpayConfiguration.getNotifyUrl());
		params.put("nonce_str", WechatpayUtils.genParamNonceStr());
		params.put("out_trade_no", WechatpayUtils.genOutTradeNo());
		params.put("total_fee", (int) Math.ceil(rechargeMode.getTotalMoney() * 100));
		params.put("spbill_create_ip", IPUtils.getIPAddress(((ServletRequestAttributes) 
			RequestContextHolder.getRequestAttributes()).getRequest()));
		params.put("time_start", WechatpayUtils.genParamTimeV1());
		params.put("sign", WechatpayUtils.genSignature(params, wechatpayConfiguration.getMchSecret()));
		String respTxt = HttpClientUtils.sendPost(ORDER_URL, WechatpayUtils
			.genRequestParams(params), 5000, ContentType.APPLICATION_XML, "UTF-8", null);
		Map<String, String> resultMap = null;
		try {
			resultMap = XMLUtils.parseXML(respTxt);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(ResultCode.SYSTEM_IS_BUSY);
		}
		if (!"SUCCESS".equals(resultMap.get("return_code")) || 
				!"SUCCESS".equals(resultMap.get("result_code"))) {
			throw new BusinessException("获取prepayid失败!" + resultMap.get("return_msg"));
		}
		TreeMap<String, Object> sparams = new TreeMap<String, Object>();
		sparams.put("appid", wechatpayConfiguration.getAppId());
		sparams.put("partnerid", wechatpayConfiguration.getMchId());
		sparams.put("prepayid", resultMap.get("prepay_id"));
		sparams.put("package", wechatpayConfiguration.getPackageWechat());
		sparams.put("noncestr", resultMap.get("nonce_str"));
		sparams.put("timestamp", WechatpayUtils.genParamTimeV2());
		String secondSign = WechatpayUtils.genSignature(sparams, wechatpayConfiguration.getMchSecret());
		sparams.put("sign", secondSign);
		insertRechargeRecord(rechargeMode, (String) params.get("out_trade_no"), sparams);
		return sparams;
	}

	@Override
	public String verifyPaymentNotifyCallback(Object callbackParams) throws BusinessException {
		Map<String, String> notifyParams = null;
		try {
			notifyParams = XMLUtils.parseXML((String) callbackParams);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(ResultCode.SYSTEM_IS_BUSY);
		}
		if (!"SUCCESS".equals(notifyParams.get("return_code"))) {
			LOG.error("通信标识失败! {}", notifyParams.get("return_msg"));
			return WechatpayUtils.genResponse("FAIL", "通信标识失败!");
		}
		if (!wechatpayConfiguration.getMchId().equals(notifyParams.get("mch_id"))) {
			LOG.error("商户ID不正确! {}", notifyParams.get("return_msg"));
			return WechatpayUtils.genResponse("FAIL", "商户ID不正确!");
		}
		int cashFee = Integer.parseInt(notifyParams.get("cash_fee"));
		int totalFee = Integer.parseInt(notifyParams.get("total_fee"));
		if (cashFee != totalFee) {
			LOG.error("支付金额与订单金额不一致! {}", notifyParams.get("return_msg"));
			return WechatpayUtils.genResponse("FAIL", "支付金额与订单金额不一致!");
		}
		String sign = notifyParams.remove("sign");
		TreeMap<String, Object> vparams = new TreeMap<String, Object>();
		vparams.putAll(notifyParams);
		String vsign = WechatpayUtils.genSignature(vparams, wechatpayConfiguration.getMchSecret());
		if (!sign.equals(vsign)) {
			LOG.error("签名错误! {}", notifyParams.get("return_msg"));
			return WechatpayUtils.genResponse("FAIL", "签名错误!");
		}
		String outTradeNo = notifyParams.get("out_trade_no");
		RechargeRecord rechargeRecord = rechargeRecordService.readRechargeRecordByOutTradeNo(outTradeNo);
		if (null == rechargeRecord) {
			LOG.error("商户生成的订单号未找到记录! {}", notifyParams.get("return_msg"));
			return WechatpayUtils.genResponse("FAIL", "商户生成的订单号未找到记录!");
		}
		double actualCashFee = Double.parseDouble(String.valueOf(cashFee)) / 100;
		if (!rechargeRecord.getTotalMoney().equals(actualCashFee)) {
			LOG.error("支付金额与订单金额不一致! {}", notifyParams.get("return_msg"));
			LOG.error("{} , {}", rechargeRecord.getTotalMoney(), actualCashFee);
			return WechatpayUtils.genResponse("FAIL", "支付金额与订单金额不一致!");
		}
		if (!"SUCCESS".equals(notifyParams.get("result_code"))) {
			LOG.error("业务标识失败! {}", notifyParams.get("return_msg"));
			updateRechargeRecordFailure(rechargeRecord, notifyParams);
			return WechatpayUtils.genResponse("FAIL", "业务标识失败!");
		}
		updateRechargeRecordSuccess(rechargeRecord, notifyParams);
		return WechatpayUtils.genResponse("SUCCESS", "OK");
	}

	@Override
	public void updateOrderByOrderNo(String orderNo, boolean isMch) throws BusinessException {
		TreeMap<String, Object> params = new TreeMap<String, Object>();
		params.put("appid", wechatpayConfiguration.getAppId());
		params.put("mch_id", wechatpayConfiguration.getMchId());
		params.put(isMch ? "out_trade_no" : "transaction_id", orderNo);
		params.put("nonce_str", WechatpayUtils.genParamNonceStr());
		params.put("sign", WechatpayUtils.genSignature(params, wechatpayConfiguration.getMchSecret()));
		String respTxt = HttpClientUtils.sendPost(ORDER_QUERY_URL, WechatpayUtils
			.genRequestParams(params), 5000, ContentType.APPLICATION_XML, "UTF-8", null);
		Map<String, String> resultMap = null;
		try {
			resultMap = XMLUtils.parseXML(respTxt);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(ResultCode.SYSTEM_IS_BUSY);
		}
		if (!"SUCCESS".equals(resultMap.get("return_code"))) return;
		String outTradeNo = resultMap.get("out_trade_no");
		if (!"SUCCESS".equals(resultMap.get("result_code"))) {
			RechargeRecord rechargeRecord = new RechargeRecord();
			rechargeRecord.setOutTradeNo(outTradeNo);
			rechargeRecord.setResultNote(GsonUtils.fromMapExtToJson(resultMap));
			rechargeRecord.setTradeStatus(TradeStatus.FAILURE.value());
			rechargeRecordService.update(rechargeRecord);
		} else {
			RechargeRecord chargingRecord = rechargeRecordService.readRechargeRecordByOutTradeNo(outTradeNo);
			updateRechargeRecordSuccess(chargingRecord, resultMap);
		}
	}
	
	/** 保存充值记录 */
	private void insertRechargeRecord(RechargeMode rechargeMode, String outTradeNo, Map<String, Object> params) {
		RechargeRecord rechargeRecord = new RechargeRecord();
		//TODO
		/** rechargeRecord.setUserId(WebUtils.getCurrentUser().getId()); **/
		rechargeRecord.setTotalMoney(rechargeMode.getTotalMoney());
		rechargeRecord.setRechargeMode(GsonUtils.builder().toJson(rechargeMode));
		rechargeRecord.setChannel("wechatpay");
		rechargeRecord.setOutTradeNo(outTradeNo);
		rechargeRecord.setSign((String) params.get("sign"));
		rechargeRecord.setRechargeNote(GsonUtils.fromMapToJson(params));
		rechargeRecord.setTradeStatus(TradeStatus.INITIAL.value());
		rechargeRecord.setInsertTime(new Date());
		rechargeRecordService.insert(rechargeRecord);
	}
	
	/** 充值失败处理 */
	private void updateRechargeRecordFailure(RechargeRecord rechargeRecord, Map<String, String> notifyParams) {
		RechargeRecord uRechargeRecord = new RechargeRecord();
		uRechargeRecord.setUserId(rechargeRecord.getUserId());
		uRechargeRecord.setOutTradeNo(rechargeRecord.getOutTradeNo());
		uRechargeRecord.setResultNote(GsonUtils.fromMapExtToJson(notifyParams));
		uRechargeRecord.setTradeNo(notifyParams.get("transaction_id"));
		uRechargeRecord.setTradeStatus(TradeStatus.FAILURE.value());
		rechargeRecordService.update(uRechargeRecord);
	}
	
	/** 充值成功处理 */
	private void updateRechargeRecordSuccess(RechargeRecord rechargeRecord, Map<String, String> notifyParams) {
		String dbTradeStatus = rechargeRecord.getTradeStatus();
		if (TradeStatus.SUCCESS.value().equals(dbTradeStatus) || 
			TradeStatus.FINISHED.value().equals(dbTradeStatus)) return;
		RechargeRecord uRechargeRecord = new RechargeRecord();
		uRechargeRecord.setUserId(rechargeRecord.getUserId());
		uRechargeRecord.setOutTradeNo(rechargeRecord.getOutTradeNo());
		uRechargeRecord.setTradeNo(notifyParams.get("transaction_id"));
		uRechargeRecord.setResultNote(GsonUtils.fromMapExtToJson(notifyParams));
		uRechargeRecord.setTradeStatus(TradeStatus.SUCCESS.value());
		rechargeRecordService.update(uRechargeRecord);
		/**
		RechargeMode rechargeMode = rechargeRecord.obtainRechargeMode();
		userBizService.updateDCoinByUserId(rechargeRecord.getUserId(), rechargeMode.getdCoin());
		**/
	}
	
}
