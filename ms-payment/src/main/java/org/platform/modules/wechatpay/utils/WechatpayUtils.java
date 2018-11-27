package org.platform.modules.wechatpay.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.UUID;

import org.platform.utils.endecrypt.MD5Utils;
import org.platform.utils.exception.BusinessException;

public class WechatpayUtils {
	
	private static Random random = new Random();
	
	private static SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyyMMdd");
	
	private static SimpleDateFormat SDF_2 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private static String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	/** 创建订单ID */
	public static String genOutTradeNo() throws BusinessException {
		return SDF_1.format(new Date()) + MD5Utils.hash(UUID.randomUUID().toString());
	}
	
	/** 微信签名 */
	public static String genSignature(SortedMap<String, Object> params, String mchSecretKey) {
		StringBuilder sb = new StringBuilder(100);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue()+ "&");
		}
		sb.append("key=").append(mchSecretKey);
		return MD5Utils.hash(sb.toString()).toUpperCase();
	}
	
	/** 生成微信请求参数 */
	public static String genRequestParams(SortedMap<String, Object> params) {
		StringBuilder sb = new StringBuilder(100);
        sb.append("<xml>");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramKey = (String) entry.getKey();
            Object paramValue = entry.getValue();
            if ("attach".equalsIgnoreCase(paramKey) || "body".equalsIgnoreCase(paramKey)) {
                sb.append("<" + paramKey + ">" + "<![CDATA[" + paramValue + "]]></" + paramKey + ">");
            } else {
                sb.append("<" + paramKey + ">" + paramValue + "</" + paramKey + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
	}
	
	/** 生成微信响应 */
	public static String genResponse(String returnCode, String returnMsg) {
        return "<xml><return_code><![CDATA[" + returnCode + "]]></return_code>"
        	+ "<return_msg><![CDATA[" + returnMsg + "]]></return_msg></xml>";
    }
	
	/** 生成微信参数nonce-str */
	public static String genParamNonceStr() {
        StringBuilder sb = new StringBuilder(26);
        for (int i = 0; i < 16; i++) {
        	sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
	
	/** 生成微信参数time-start */
	public static String genParamTimeV1() {
		return SDF_2.format(new Date());
	}
	
	/** 生成微信参数timestamp */
	public static String genParamTimeV2() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}

}
