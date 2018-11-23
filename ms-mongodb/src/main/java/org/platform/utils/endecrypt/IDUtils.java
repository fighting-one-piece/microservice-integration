package org.platform.utils.endecrypt;

import java.util.Random;
import java.util.UUID;

public class IDUtils {

	/**  
     * 生成32位编码  
     * @return string  
     */    
    public static String genUUID(){    
        return UUID.randomUUID().toString().trim().replaceAll("-", "");    
    }    
    
    /**
     * 生成随机的指定长度的ID
     * @param length
     * @return
     */
    public static String genRandomID(int length) { 
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
        Random random = new Random();   
        StringBuilder sb = new StringBuilder();   
        for (int i = 0, baseLen = base.length(); i < length; i++) {   
            sb.append(base.charAt(random.nextInt(baseLen)));   
        }   
        return sb.toString();   
    }  
	
}
