package org.platform.utils.token;

import org.platform.utils.endecrypt.Base32Utils;
import org.platform.utils.endecrypt.Base64Utils;
import org.platform.utils.endecrypt.MD5Utils;
import org.platform.utils.endecrypt.SHAUtils;

public class TokenUtils {

	/** 认证KEY */
	public static final String AUTHENTICATION = "authentication";
	/** 授权KEY */
	public static final String AUTHORIZATION = "authorization";

	/**
	 * 16位MD5摘要
	 * @param params
	 * @return
	 */
	public static String gen16MD5Token(String... params) {
		return gen32MD5Token(params).substring(8, 24);
	}
	
	/**
	 * 32位MD5摘要
	 * @param params
	 * @return
	 */
	public static String gen32MD5Token(String... params) {
		StringBuilder sb = new StringBuilder();
		if (null != params && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i]);
			}
		}
		return MD5Utils.hash(SHAUtils.SHA512(sb.toString()));
	}

	/**
	 * 认证MD5摘要
	 * @param params
	 * @return
	 */
	public static String genAuthenticationMD5Token(String... params) {
		StringBuilder sb = new StringBuilder(AUTHENTICATION);
		if (null != params && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i]);
			}
		}
		return MD5Utils.hash(SHAUtils.SHA512(sb.toString()));
	}
	
	/**
	 * 认证MD5摘要
	 * @param params
	 * @return
	 */
	public static String genNAuthenticationMD5Token(int n, String... params) {
		StringBuilder sb = new StringBuilder(AUTHENTICATION + n);
		if (null != params && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i]);
			}
		}
		return n + MD5Utils.hash(SHAUtils.SHA512(sb.toString()));
	}

	/**
	 * 授权MD5摘要
	 * @param params
	 * @return
	 */
	public static String genAuthorizationMD5Token(String... params) {
		StringBuilder sb = new StringBuilder(AUTHORIZATION);
		if (null != params && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i]);
			}
		}
		return MD5Utils.hash(SHAUtils.SHA512(sb.toString()));
	}

	/**
	 * Base32摘要
	 * @param params
	 * @return
	 */
	public static String genBase32Token(String... params) {
		StringBuilder sb = new StringBuilder();
		if (null != params && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i]);
			}
		}
		return Base32Utils.encode(SHAUtils.SHA512(sb.toString()));
	}

	/**
	 * Base64摘要
	 * @param params
	 * @return
	 */
	public static String genBase64Token(String... params) {
		StringBuilder sb = new StringBuilder();
		if (null != params && params.length > 0) {
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i]);
			}
		}
		return Base64Utils.encode(SHAUtils.SHA512(sb.toString()));
	}

	/**
	 * 验证Token是否正确
	 * @param token
	 * @param params
	 * @return
	 */
	public static boolean authenticationMD5Token(String token, String... params) {
		return token.equals(gen32MD5Token(params)) ? true : false;
	}

	/**
	 * 验证是否是认证Token
	 * @param token
	 * @param params
	 * @return
	 */
	public static boolean isAuthenticationMD5Token(String token, String... params) {
		return token.equals(genAuthenticationMD5Token(params)) ? true : false;
	}
	
	/**
	 * 验证是否是认证Token
	 * @param token
	 * @param params
	 * @return
	 */
	public static boolean isNAuthenticationMD5Token(String token, String... params) {
		return token.equals(genNAuthenticationMD5Token(Integer.parseInt(token.substring(0, 1)), params)) ? true : false;
	}

	/**
	 * 验证是否是授权Token
	 * @param token
	 * @param params
	 * @return
	 */
	public static boolean isAuthorizationMD5Token(String token, String... params) {
		return token.equals(genAuthorizationMD5Token(params)) ? true : false;
	}

}
