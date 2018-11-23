package org.platform.utils.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class RegexUtils {
	//纬度
	public static final String LATITUDE_REX = "[\\-\\+]?([0-8]?\\d{1}\\.\\d{1,5}|90\\.0{1,5})";
	//经度
	public static final String LONGITUDE_REX = "[\\-\\+]?(0?\\d{1,2}\\.\\d{1,5}|1[0-7]?\\d{1}\\.\\d{1,5}|180\\.0{1,5})";
	
	/**
	 * 是否为纬度
	 * @param latitude
	 * @return
	 */
	public static boolean isLatitude(String latitude){
		if(StringUtils.isBlank(latitude)) return false;
		return latitude.matches(LATITUDE_REX);
	}
	/**
	 * 是否为经度
	 * @param longtitude
	 * @return
	 */
	public static boolean isLongtitude(String longtitude){
		if (StringUtils.isBlank(longtitude)) return false;
		return longtitude.matches(LONGITUDE_REX);
	}
	
	/**
	 * 判断字符是否是手机号码
	 * @param phone 需要判断是否是手机号码的字符
	 * @return
	 */
	public static boolean isMobilePhone(String mobilePhone) {
		if (StringUtils.isBlank(mobilePhone)) return false;
		Pattern pattern = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$");
		Matcher matcher = pattern.matcher(mobilePhone);
		if (matcher.find()) return true;
		return false;
	}

	/**
	 * 判断字符是否是身份证
	 * @param idCard 需要判断是否是身份证的字符
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		// 对身份证号进行长度等简单判断
		if (StringUtils.isBlank(idCard) || (idCard.length() != 15 && idCard.length() != 18)) return false;
		Pattern pattern1 = Pattern.compile("^[1-9]\\d{7}((0[1-9])||(1[0-2]))((0[1-9])||(1\\d)||(2\\d)||(3[0-1]))\\d{3}$");
		Pattern pattern2 = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X|x)$");
		Matcher matcher = pattern1.matcher(idCard);
		Matcher matcher2 = pattern2.matcher(idCard);
		if (!matcher.find() && !matcher2.find()) return false;
		if (idCard.length() == 15) return true;
		// 1-17位相乘因子数组
		int[] factor = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		// 18位随机码数组
		char[] random = "10X98765432".toCharArray();
		// 计算1-17位与相应因子乘积之和
		int total = 0;
		for (int i = 0; i < 17; i++) {
			total += Character.getNumericValue(idCard.charAt(i)) * factor[i];
		}
		// 判断随机码是否相等
		return random[total % 11] == idCard.toUpperCase().charAt(17);
	}

	/**
	 * 验证字符串是否全为中文 位数在2-10位
	 * @param str 需要判断是否全为中文的字符
	 * @return
	 */
	public static boolean isChineseName(String str) {
		if (StringUtils.isBlank(str)) return false;
		Pattern pattern = Pattern.compile("^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]){2,10}$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) return true;
		return false;
	}

	/**
	 * 验证是否为邮箱
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (StringUtils.isBlank(email)) return false;
		Pattern pattern = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
		Matcher matcher = pattern.matcher(email);
		if (matcher.find()) return true;
		return false;
	}
	
	
	/*
	 * 校验过程： 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
	 * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
	 * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
	 */
	/**
	 * @author ljp 校验银行卡卡号
	 * @param bankCard 银行卡号
	 * @return true或false
	 */
	public static boolean checkBankCard(String bankCard) {
		if (bankCard.length() < 15 || bankCard.length() > 19) {
			return false;
		}
		char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return bankCard.charAt(bankCard.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * @author ljp
	 * @param nonCheckCodeBankCard 不包含校验位的银行卡号
	 * @return 银行卡校验位
	 */
	private static char getBankCardCheckCode(String nonCheckCodeBankCard) {
		if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
				|| !nonCheckCodeBankCard.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeBankCard.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}
}
