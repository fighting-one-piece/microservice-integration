package org.platform.utils.endecrypt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(AESUtils.class);
	
	public static byte[] encrypt(String content, String password) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] encoded = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return cipher.doFinal(content.getBytes("utf-8"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return null;
	}

	public static byte[] decrypt(byte[] content, String password) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128, new SecureRandom(password.getBytes()));
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] encoded = secretKey.getEncoded();
			SecretKeySpec secretKeySpec = new SecretKeySpec(encoded, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			return cipher.doFinal(content);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return null;
	}

}
