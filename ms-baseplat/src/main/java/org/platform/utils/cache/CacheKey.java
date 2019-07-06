package org.platform.utils.cache;

public class CacheKey {

	public interface USER {
		/** ID缓存用户信息 */
		public static final String ID = "bp:user:id:%s";
		/** ACCOUNT缓存用户信息 */
		public static final String ACCOUNT = "bp:user:account:%s";
		/** ACCESSId缓存用户信息 */
		public static final String ACCESSID = "bp:user:accessid:%s";
		/** MOBILEPHONE缓存用户信息 */
		public static final String MOBILEPHONE = "bp:user:mobilephone:%s";
	}

}
