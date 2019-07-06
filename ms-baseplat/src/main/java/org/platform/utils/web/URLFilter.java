package org.platform.utils.web;

import java.util.HashSet;
import java.util.Set;

public class URLFilter {
	
	public interface NOT_AUTH {
		public static final String URL_01 = "/login";
		public static final String URL_02 = "/jcaptcha.jpg";
		public static final String URL_03 = "/jcaptcha-validate";
		public static final String URL_04 = "/verificationCode.jpg";
		
		public static final String URL_100 = "/hystrix";
		public static final String URL_101 = "/actuator";
		public static final String URL_102 = "/actuator/env";
		public static final String URL_103 = "/actuator/info";
		public static final String URL_104 = "/actuator/health";
		public static final String URL_105 = "/actuator/hystrix.stream";
	}
	
	private static Set<String > notAuthenticationUrls = new HashSet<String>();
	
	static {
		notAuthenticationUrls.add(NOT_AUTH.URL_01);
		notAuthenticationUrls.add(NOT_AUTH.URL_02);
		notAuthenticationUrls.add(NOT_AUTH.URL_03);
		notAuthenticationUrls.add(NOT_AUTH.URL_04);
		notAuthenticationUrls.add(NOT_AUTH.URL_100);
		notAuthenticationUrls.add(NOT_AUTH.URL_101);
		notAuthenticationUrls.add(NOT_AUTH.URL_102);
		notAuthenticationUrls.add(NOT_AUTH.URL_103);
		notAuthenticationUrls.add(NOT_AUTH.URL_104);
		notAuthenticationUrls.add(NOT_AUTH.URL_105);
	}
	
	public static Set<String> notAuthenticationUrls() {
		return notAuthenticationUrls;
	}
	
}
