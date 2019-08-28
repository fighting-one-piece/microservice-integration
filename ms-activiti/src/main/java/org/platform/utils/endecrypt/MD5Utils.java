package org.platform.utils.endecrypt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Utils {
	
	private static Logger LOG = LoggerFactory.getLogger(MD5Utils.class);

    /**
     * @param input
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws UnsupportedEncodingException 
     */
    private static byte[] md5(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(input.getBytes("UTF-8"));
        return algorithm.digest();
    }

    /**
     * @param hash
     * @return
     */
    private static final String toHex(byte hash[]) {
        if (hash == null)  return null;
        StringBuffer buf = new StringBuffer(hash.length * 2);
        for (int i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * @param input
     * @return
     */
    public static String hash(String input) {
        try {
            return new String(toHex(md5(input)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
        	LOG.error(e.getMessage(), e);
            return input;
        }
    }

}
