package org.cisiondata.utils.regex;

import org.apache.commons.lang.StringUtils;

public class RegexUtils {
	// 手机号码
	public static final String MOBILE_PHONE_REX = "1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}";
	// 纬度
	public static final String LATITUDE_REX = "[\\-\\+]?([0-8]?\\d{1}\\.\\d{1,5}|90\\.0{1,5})";
	// 经度
	public static final String LONGITUDE_REX = "[\\-\\+]?(0?\\d{1,2}\\.\\d{1,5}|1[0-7]?\\d{1}\\.\\d{1,5}|180\\.0{1,5})";

	public static final String ZIPCODE_REX = "^[0-9]{6}$";

	public static boolean isMoblePhone(String phone) {
		if (StringUtils.isBlank(phone))
			return false;
		return phone.matches(MOBILE_PHONE_REX);
	}

	/**
	 * 是否为纬度
	 * 
	 * @param latitude
	 * @return
	 */
	public static boolean isLatitude(String latitude) {
		if (StringUtils.isBlank(latitude))
			return false;
		return latitude.matches(LATITUDE_REX);
	}

	/**
	 * 是否为经度
	 * 
	 * @param longtitude
	 * @return
	 */
	public static boolean isLongtitude(String longtitude) {
		if (StringUtils.isBlank(longtitude))
			return false;
		return longtitude.matches(LONGITUDE_REX);
	}

	/**
	 * 验证是否为邮编
	 * 
	 * @param zipCode
	 * @return
	 */
	public static boolean isZipCode(String zipCode) {
		if (StringUtils.isBlank(zipCode))
			return false;
		return zipCode.matches(ZIPCODE_REX);
	}
}
