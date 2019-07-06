package org.platform.utils.endecrypt;

public class EndecryptUtils {
	
	public static final String SALT = "baseplat";
	
	public static String encryptPassword(String password) {
		return SHAUtils.SHA512(new StringBuilder(100).append(password).append(SALT).toString());
	}

    public static String encryptPassword(String password, String salt) {
    	return SHAUtils.SHA512(new StringBuilder(100).append(password).append(salt).toString());
    }
    
    public static String encryptPassword(String account, String password, String salt) {
    	return SHAUtils.SHA512(new StringBuilder(100).append(account).append(password).append(salt).toString());
    }
    
}
