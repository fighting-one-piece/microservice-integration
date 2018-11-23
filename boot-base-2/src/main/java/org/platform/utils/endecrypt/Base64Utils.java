package org.platform.utils.endecrypt;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base64Utils {
	
	private static Logger LOG = LoggerFactory.getLogger(Base64Utils.class);

	public static String encode(byte[] input) {
		return Base64.encodeBase64String(input);
	}
	
	public static String encode(String input) {
		return Base64.encodeBase64String(input.getBytes());
	}
	
	public static String encodeWithSunMisc(byte[] input) {
		return new sun.misc.BASE64Encoder().encode(input);
	}

	public static String encodeWithSunMisc(String input) {
		return new sun.misc.BASE64Encoder().encode(input.getBytes());
	}

	public static byte[] decode(String input) {
		return Base64.decodeBase64(input);
	}
	
	public static String decode(String input, String charset) {
		try {
			return new String(Base64.decodeBase64(input), charset);
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static byte[] decodeWithSunMisc(String input) {
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			return decoder.decodeBuffer(input);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static String decodeWithSunMisc(String input, String charset) {
		try {
			sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
			return new String(decoder.decodeBuffer(input), charset);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
}
