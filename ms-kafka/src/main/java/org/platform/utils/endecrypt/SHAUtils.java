package org.platform.utils.endecrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAUtils {

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String SHA(String input) {
		return SHA(input, "SHA");
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String SHA1(String input) {
		return SHA(input, "SHA-1");
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String SHA256(String input) {
		return SHA(input, "SHA-256");
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static String SHA512(String input) {
		return SHA(input, "SHA-512");
	}

	/**
	 * 
	 * @param input
	 * @param type
	 * @return
	 */
	public static String SHA(String input, String type) {
		if (input != null && input.length() > 0) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance(type);
				messageDigest.update(input.getBytes());
				byte[] digest = messageDigest.digest();
				StringBuilder sb = new StringBuilder();
				for (int i = 0, len = digest.length; i < len; i++) {
					String hex = Integer.toHexString(0xff & digest[i]);
					if (hex.length() < 2) sb.append('0');
					sb.append(hex);
				}
				return sb.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
}
