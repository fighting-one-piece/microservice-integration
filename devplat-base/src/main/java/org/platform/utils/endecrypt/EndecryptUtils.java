package org.platform.utils.endecrypt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.platform.utils.date.DateFormatter;

public class EndecryptUtils {
	
	public static void main(String[] args) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("appId", "123456");
		params.put("appKey", "abcedfg");
		params.put("mobilePhone", "15828225532");
		params.put("areaCode", "111111");
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
			String paramName = entry.getKey();
			sb.append(paramName).append("=").append(entry.getValue()).append("&");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		System.err.println(sb.toString());
		String signatureGen = SHAUtils.SHA1(sb.toString());
		System.err.println(signatureGen);
		String url = "/xxxxxx/xxxxxx?param1=xxxxxx&param2=xxxxxx&signature=ad85c9e40294f3fefa2c898991ef79b6afb958d4";
		String encryptTxt = RSAUtils.encryptNoPaddingAndBase64(url);
		System.err.println(encryptTxt);
	}

}
