package org.platform.utils.cache;

public class CacheUtils {

	/**
	 * 生成缓存key
	 * @param format
	 * @param args
	 * @return
	 */
	public static String genCacheKey(String format, Object... args) {
		return String.format(format, args);
	}
	
}
