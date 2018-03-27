package org.cisiondata.utils.idgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.cisiondata.utils.endecrypt.MD5Utils;

public class IDGenerator {
	
	public static String generateByMapValues(Map<String, Object> map, String... excludeKeys) {
		List<Map.Entry<String, Object>> entries = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
		entries.sort(new Comparator<Map.Entry<String, Object>>() {
			@Override
			public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		List<String> excludeKeyList = Arrays.asList(excludeKeys);
		StringBuilder sb = new StringBuilder(100);
		for (int i = 0, len = entries.size(); i < len; i++) {
			Map.Entry<String, Object> entry = entries.get(i);
			if (excludeKeyList.contains(entry.getKey())) continue;
			Object value = entry.getValue();
			if (null == value) continue;
			sb.append(value);
		}
		return MD5Utils.hash(sb.toString());
	}
	
}
