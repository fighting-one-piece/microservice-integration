package org.platform.modules.alipay.utils;

import java.util.UUID;

import org.platform.utils.endecrypt.MD5Utils;
import org.platform.utils.exception.BusinessException;

public class AlipayUtils {
	
	/** 创建订单ID */
	public static String genOutTradeNo() throws BusinessException {
		return MD5Utils.hash(UUID.randomUUID().toString());
	}

}
