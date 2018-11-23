package org.platform.utils.param;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.utils.date.DateFormatter;
import org.platform.utils.endecrypt.SHAUtils;
import org.platform.utils.exception.BusinessException;

public class ParamsUtils {
	
	public static final String APP_ID = "9A27E1f5F3AEeE71";
	
	public static final String APP_KEY = "8FD098f28cC93B18AB8816A30046D504";
	
	public static String genToken(Map<String, String> params) {
		params.put("accessId", ParamsUtils.APP_ID);
		params.put("accessKey", ParamsUtils.APP_KEY);
		params.put("date", DateFormatter.DATE.get().format(Calendar.getInstance().getTime()).replace("-", ""));
		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(params.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		StringBuilder sb = new StringBuilder(100);
		for (Map.Entry<String, String> entry : list) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return SHAUtils.SHA1(sb.toString());
	}

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
