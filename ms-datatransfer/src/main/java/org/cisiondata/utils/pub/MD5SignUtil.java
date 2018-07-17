package org.cisiondata.utils.pub;

import java.util.Date;

import org.cisiondata.utils.date.DateFormatter;

public class MD5SignUtil {
	
	public static String SignEncryption(){
		String sign = "";
		String currentDate = DateFormatter.DATE.get().format(new Date());
		currentDate = currentDate.replaceAll("-", "");
		//sign = MD5Utils.hash(Constants.XINSHU_SIGN_VALUE + currentDate);
		return sign;
	}
	
}
