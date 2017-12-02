package org.cisiondata.utils.endecrypt;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.junit.Assert;

public class RSAUtils {

	private static final String ALGORITHM = "RSA";
	private static final String PRIVATE_KEY_PATH = "D:\\rsa_private.isa";
	private static final String PUBLIC_KEY_PATH = "D:\\rsa_public.isa";
	
	/**
	 * 公钥加密
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		//读取公钥对应的字节数组
		byte[] publicKeyCode = Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH));
		//构造公钥，存储起来的公钥需要使用X509EncodedKeySpec进行读取
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyCode);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		//根据已有的KeySpec生成对应的公钥
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] result = cipher.doFinal(input.getBytes());
		System.out.println(Base64.getEncoder().encodeToString(result));
		return result;
	}
	
	/**
	 * 私钥解密
	 * @throws Exception
	 */
	public static byte[] decrypt(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		byte[] privateKeyCode = Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH));
		//私钥需要通过PKCS8EncodedKeySpec来读取
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyCode);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		//生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] result = cipher.doFinal(input.getBytes());
		return result;
	}
	
	/**
	 * 私钥签名
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] sign(String data) throws Exception {
		//读取储存的私钥字节数组
		byte[] privateKeyCode = Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH));
		//包装私钥字节数组为一个KeySpec
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyCode);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		//通过KeyFactory生成私钥
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		Signature signature = Signature.getInstance("MD5withRSA");//签名的算法
		//通过私钥初始化Signature，签名时用
		signature.initSign(privateKey);
		//指定需要进行签名的内容
		signature.update(data.getBytes());
		//签名
		byte[] result = signature.sign();
		return result;
	}
	
	/**
	 * 测试签名
	 * @throws Exception
	 */
	public void testSign() throws Exception {
		byte[] sign = sign("Hello World");
		String result = Base64.getEncoder().encodeToString(sign);
		System.out.println(result);
	}
	
	/**
	 * 测试公钥验签
	 * @throws Exception
	 */
	public void testVerifySign() throws Exception {
		String data = "Hello World";
		byte[] sign = sign(data);
		Signature signature = Signature.getInstance("MD5withRSA");
		byte[] publicKeyCode = Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH));
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyCode);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		//以验签的方式初始化Signature
		signature.initVerify(publicKey);
		//指定需要验证的签名
		signature.update(data.getBytes());
		//进行验签，返回验签结果
		boolean result = signature.verify(sign);
		Assert.assertTrue(result);
	}
	
}
