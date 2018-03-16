package org.cisiondata.utils.param;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

public class ParamsUtils {

	public static void checkNotNull(String param, String message) {
		if (StringUtils.isBlank(param)) {
			throw new RuntimeException(message);
		}
	}
	
	public static <T> void checkNotNull(T param, String message) {
		if (null == param) {
			throw new RuntimeException(message);
		}
	}
	
	public static void checkNotNull(Collection<Object> params, String message) {
		if (null == params || params.size() == 0) {
			throw new RuntimeException(message);
		}
	}
	
}
