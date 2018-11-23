package org.platform.utils.endecrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

public class MD5Utils {

    private static final Logger LOG = LoggerFactory.getLogger(MD5Utils.class);

    /**
     * 
     * @param input
     * @return
     */
    private static byte[] md5(String input) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(input.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception e) {
            LOG.error("MD5 Error...", e);
        }
        return null;
    }

    /**
     * 
     * @param hash
     * @return
     */
    private static final String toHex(byte hash[]) {
        if (hash == null) {
            return null;
        }
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
     * 
     * @param input
     * @return
     */
    public static String hash(String input) {
        try {
            return new String(toHex(md5(input)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            LOG.error("not supported charset...{}", e);
            return input;
        }
    }

}
