package org.platform.utils.param;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.utils.exception.BusinessException;

public class ParamsUtils {

	public static void checkNotNull(String param, String message) {
		if (StringUtils.isBlank(param)) {
			throw new BusinessException(ResultCode.PARAM_NULL.getCode(), message);
		}
	}
	
	public static <T> void checkNotNull(T param, String message) {
		if (null == param) {
			throw new BusinessException(ResultCode.PARAM_NULL.getCode(), message);
		}
	}
	
	public static void checkNotNull(Collection<Object> params, String message) {
		if (null == params || params.size() == 0) {
			throw new BusinessException(ResultCode.PARAM_NULL.getCode(), message);
		}
	}
	
}
