package org.cisiondata.utils.endecrypt;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.cisiondata.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(RSAUtils.class);

	private static final String ALGORITHM = "RSA";
	private static final String ALGORITHM_MD5 = "MD5withRSA";
	private static final String KEY_ALGORITHM = "RSA";
    private static final String PUBLIC_KEY = "RSAPublicKey";  
	private static final String PUBLIC_KEY_PATH = "rsa/rsa_public.txt";
	private static final String PRIVATE_KEY = "RSAPrivateKey";  
	private static final String PRIVATE_KEY_PATH = "rsa/rsa_private.txt";
	
	private static final String TRANSFORMATION = "RSA/None/NoPadding";
	private static final String PROVIDER = "BC";

	private static PublicKey publicKey = null;
	private static PrivateKey privateKey = null;
	
	static {
		initializeKeyPair();
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());  
	}
	
	private static void initializeKeyPair() {
		try {
			URL pubUrl = RSAUtils.class.getClassLoader().getResource(PUBLIC_KEY_PATH);
			if (null != pubUrl) {
				Path path = Paths.get(pubUrl.toURI());
				boolean isExists = Files.exists(path);
				if (isExists) {
					//读取公钥对应的字节数组
					byte[] publicKeyCode = Base64Utils.decode(new String(Files.readAllBytes(path)));
					KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
					//构造公钥，存储起来的公钥需要使用X509EncodedKeySpec进行读取
					X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyCode);
					//根据已有的KeySpec生成对应的公钥
					publicKey = keyFactory.generatePublic(keySpec);
				}
			}
			URL priUrl = RSAUtils.class.getClassLoader().getResource(PRIVATE_KEY_PATH);
			if (null != priUrl) {
				Path path = Paths.get(priUrl.toURI());
				boolean isExists = Files.exists(path);
				if (isExists) {
					byte[] privateKeyCode = Base64Utils.decode(new String(Files.readAllBytes(path)));
					KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
					//私钥需要通过PKCS8EncodedKeySpec来读取
					PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyCode);
					//生成私钥
					privateKey = keyFactory.generatePrivate(keySpec);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 生成公钥密钥
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genRSAKeyPair() throws Exception {  
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        keyPairGenerator.initialize(1024);  
        KeyPair keyPair = keyPairGenerator.generateKeyPair();  
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        Map<String, Object> keyPairMap = new HashMap<String, Object>(2);  
        keyPairMap.put(PUBLIC_KEY, publicKey);  
        keyPairMap.put(PRIVATE_KEY, privateKey); 
        
        String rsaDirPath = System.getProperty("user.dir") + File.separator + "src" + File.separator 
        	+ "main" + File.separator + "resources" + File.separator + "rsa";
        File rsaDir = new File(rsaDirPath);
        if (!rsaDir.exists()) rsaDir.mkdirs();
        
        List<String> publicKeyLines = new ArrayList<String>();
        publicKeyLines.add(Base64Utils.encode(publicKey.getEncoded()));
        
        FileUtils.write(rsaDirPath + File.separator + "rsa_public.txt", publicKeyLines);
        
        List<String> privateKeyLines = new ArrayList<String>();
        privateKeyLines.add(Base64Utils.encode(privateKey.getEncoded()));
        FileUtils.write(rsaDirPath + File.separator + "rsa_private.txt", privateKeyLines);
        
        return keyPairMap;  
    }  
	
	/**
	 * 公钥加密
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	public static byte[] encrypt(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(input.getBytes());
	}
	
	/**
	 * 公钥加密并Base64编码
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String encryptAndBase64(String input) throws Exception {
		return Base64Utils.encode(encrypt(input));
	}
	
	/**
	 * 公钥加密,内容不变
	 * @param input
	 * @return
	 * @throws Exception 
	 */
	public static byte[] encryptNoPadding(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(input.getBytes());
	}
	
	/**
	 * 公钥加密并Base64编码,内容不变
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String encryptNoPaddingAndBase64(String input) throws Exception {
		return Base64Utils.encode(encryptNoPadding(input));
	}
	
	/**
	 * 私钥解密
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(input.getBytes());
	}
	
	/**
	 * 私钥解密
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] input) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(input);
	}
	
	/**
	 * 私钥解密并Base64解码
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptAndBase64(String input) throws Exception {
		return decrypt(Base64Utils.decode(input));
	}
	
	/**
	 * 私钥解密
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptNoPadding(String input) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(input.getBytes());
	}
	
	/**
	 * 私钥解密
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptNoPadding(byte[] input) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(input);
	}
	
	/**
	 * 私钥解密并Base64解码
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptNoPaddingAndBase64(String input) throws Exception {
		return decryptNoPadding(Base64Utils.decode(input));
	}
	
	/**
	 * 私钥签名
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static byte[] signature(String input) throws Exception {
		//签名的算法
		Signature signature = Signature.getInstance(ALGORITHM_MD5);
		//通过私钥初始化Signature，签名时用
		signature.initSign(privateKey);
		//指定需要进行签名的内容
		signature.update(input.getBytes());
		//签名
		return signature.sign();
	}
	
	/**
	 * 公钥验签
	 * @param input
	 * @throws Exception
	 */
	public static boolean verifySignature(String input) throws Exception {
		Signature signature = Signature.getInstance(ALGORITHM_MD5);
		//以验签的方式初始化Signature
		signature.initVerify(publicKey);
		//指定需要验证的签名
		signature.update(input.getBytes());
		//进行验签，返回验签结果
		return signature.verify(input.getBytes());
	}
		
}
