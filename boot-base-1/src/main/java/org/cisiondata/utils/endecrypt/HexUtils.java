package org.cisiondata.utils.endecrypt;

public class HexUtils {
	
	public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) return null;
        for (int i = 0, len = src.length; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) stringBuilder.append(0);
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
	
	public static byte[] hexStringToBytes(String src) {
        if (src == null || src.equals(""))  return null;
        src = src.toUpperCase();
        int length = src.length() / 2;
        char[] hexChars = src.toCharArray();
        byte[] dest = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            dest[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            
        }
        return dest;
    }
	
	private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
