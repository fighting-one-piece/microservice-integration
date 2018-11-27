package org.platform.modules.alipay.utils;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.platform.utils.endecrypt.MD5Utils;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.json.GsonUtils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;

public class AlipayUtils {
	
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
	
	/** 创建订单ID */
	public static String genOutTradeNo() throws BusinessException {
		return SDF.format(new Date()) + MD5Utils.hash(UUID.randomUUID().toString());
	}
	
	public static void main(String[] args) {
		String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
		String appId = "2016091900549883";
		String appPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCrDzhdYFRys7OM0Nuz5GAjCC10HEdxH8CfRGuH7Auo0HQ7B6a5g6Y9EeWN8UOvhbTAP82Ca/XBgbjVzfnIzUo7h6FbL7+YgudfP/amamGIeKB45EzALVWKDul0ls8kIbwwjNu6YwqG/ETFIMfv2/djKwbJt27M4cSXFw2YT6Adf+Fd8fVuxpPc0RZmjCJEZ1Tq6+fH5eIq3013ZvONfh7DXO/lKGp3CMGlPtt/K7L+vv86oI1cn1MxDLL07otlIfBNyQJGuM6Lq1g8JppMKLBULo46rXVa0psWsK0Qu5F8ojCmQpq/LbZI4CJvR9ze9KNr2HTEaRZ+fqSGGmPFhZkFAgMBAAECggEBAIugMsvVM1kk4WwRlyOxR9+QbzmVU6M83cei23ro62NyM01zo11N3kV+9Db2Nd9xbeZ2rdLFOKYosqyiIj8Lgoow3T5HLUbZou4WkceokdAJVkMXZqZ+2pGENn7aEl5VwvyGHVx4N0GTbLbTWR+qFrY1iqbgOWq25fbF9Gzcob8xKr167qpBFVzRUUIuIidwQ4W6oj3UXABfDkCCJ7b/sK/cr42KZH7CH5mO3xTsd70ePSzf7hiQln4SGwZqaUymeuASJjDhU62YHiZy4UJDQW1AWqYZipz8ShLsArjnKeWivCBKeir2L3D/lIttw96q6Tez54QAKX8rD2XgqdQcF4ECgYEA3CXNI3t0en5xPH6V0UY5bz5OIfEKdbcXj5IMqm2V4wCg2abjneVwst30oRSfsEwbtn4CqPfIZFXe3I3zApm/8co7/iN102X+qk1ZLNXA4PeDsP/oya3nR3n3BaCfm0Ys+MwmogjrguwdB88Yfa3A6EZZJDkMpVbNFo0to2BRLxECgYEAxurgmxBQHqSQ7BWLALsrZ3Cwwxv6KsMItTeJ0KMj6UJl32PJuSmXOSuy5TlmsHvENSE0VjFVd+cN04dHQ0YkxQvDRhtbsO2M2NGJ2NOPKJdveb6jjQuS4ksaFdPQB6XG8Bx2mUVdcDDm80EzBZW5a/Evfq2IHh5qnEHz2F4OMrUCgYARUrQqt5s5zFtSvE9DsXfxhgCV8Sa2jpq/BGoUuxzRSQpfNh+xHA3bNVX7mujZ8ZM4r/+7lgvhwVQRHOZjbP/KofRIkjfCU8NPWVjMC2HrEe797ZU+0X1ihXlnqvUEPehqB35+v57wpW2/YI93zEViwa5gCFEzbXoj7SYLNhgNUQKBgQCBiHAs6gmSes3YvwqetkPEf3DMDUryT8E66Oi40ZPQESnvt7aj+j58v0Um2iNR57bUdkPzoPqsMu0hAyXX3DSTkF5gJ+6kn3ypsIHYjaPa5gp0uD0e2xuSex+IeT8ty7lpoM9mbfIz3dR3Mcz85QwzC4NHNc++9UWtbZFSI6WRRQKBgEr1kZpqTfXpY/apdpjYRsidNHGXybUdngyKNA+YAF4Wlvz844fujZwCEiOSugvsMgWjCIl5kgSf7EOZoYOnfpZqKnpG4V9xP+kROHSmSGbiGXIWTI8dV0yM7A3ehzUin1JL5Ke0x/J6EcImD8vaWhDSBeS/Db+X4a07+VrsiCex";
		String format = "json";
		String charset = "UTF-8";
		String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuds8BhDZH4D9tQXM+4XV/8JtOk7b2LeU99BRzox3AZEtccjTEzeWZd5av34jOCdg2tTD1HyrFeTs3Phws50qmJbKNHeushqPUTvJcVfi+05OvWg9fgFV+Mdy+jukFd1JHpPKWs3IkGDn/+QhX6jftrWzvklhIcrvOe/mJUHHCnosJIDaqF2d+xC/o8HxOZVz2TOLzM5xm6aK0Mxs5lU+4IRSYNoTicv4ppOp85pBfJBZ8MVEcYOVku1smBrI+wIhsYa5pl+1qsWFFqNu3dMMLTxAXJXp2dLIIP6B6Nkjq/GlXbYP4JKnVzKLhjeNggJyRjakwEtITGrf4KBbRtDg8QIDAQAB";
		String signType = "RSA2";
		AlipayClient alipayClient =  new DefaultAlipayClient(gatewayUrl, appId, appPrivateKey, format, charset, alipayPublicKey, signType);
		
		// 1、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl("http://47.100.204.233:8801/api/v1/recharge/alipay/return");
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl("http://47.100.204.233:8801/api/v1/recharge/alipay/notify");

        // 2、SDK已经封装掉了公共参数，这里只需要传入业务参数，请求参数查阅开头Wiki
        Map<String, String> map = new HashMap<String, String>(16);
        map.put("out_trade_no", AlipayUtils.genOutTradeNo());
        map.put("total_amount", String.format("%.2f", 0.01d));
        map.put("subject", "payment subject");
        map.put("body", "payment body");
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("passback_params", "merchantBizType%3d3C%26merchantBizNo%3d2016010101111");
        
//        alipayRequest.setBizContent("{" +
//                "    \"out_trade_no\":\"20150320010101001\"," +
//                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
//                "    \"total_amount\":0.01," +
//                "    \"subject\":\"Iphone6 16G\"," +
//                "    \"body\":\"Iphone6 16G\"," +
//                "    \"passback_params\":\"merchantBizType%3d3C%26merchantBizNo%3d2016010101111\"," +
//                "    \"extend_params\":{" +
//                "    \"sys_service_provider_id\":\"2088511833207846\"" +
//                "    }"+
//                "  }");
        
        alipayRequest.setBizContent(GsonUtils.fromMapExtToJson(map));
        try{
//        	alipayRequest.setBizContent(URLEncoder.encode(GsonUtils.fromMapExtToJson(map), "UTF-8"));
            // 3、生成支付表单
            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(alipayRequest);
            if(alipayResponse.isSuccess()) {
            	String result = alipayResponse.getBody();
            	System.err.println("######");
            	System.err.println(URLDecoder.decode(result, "UTF-8"));
                System.err.println(result);
            } else {
                System.err.println("【支付表单生成】失败，错误信息：" + alipayResponse.getSubMsg());
                System.err.println("error");
            }
        } catch (Exception e) {
        	System.err.println("【支付表单生成】异常，异常信息：" + e.getMessage());
            e.printStackTrace();
        }
        
	}

}
