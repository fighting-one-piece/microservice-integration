package org.platform.utils.date;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public enum DateFormatter {

	TIME("yyyy-MM-dd HH:mm:ss"), 
	MINUTE("yyyy-MM-dd HH:mm"), 
	HOUR("yyyy-MM-dd HH"), 
	DATE("yyyy-MM-dd"), 
	MONTH("yyyy-MM");

	private String formatStr = null;
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

	private DateFormatter(String s) {
		this.formatStr = s;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public java.text.SimpleDateFormat get() {
		ThreadLocal<SimpleDateFormat> res = (ThreadLocal) sdfMap.get(this.formatStr);
		if (null == res) {
			synchronized (sdfMap) {
				if (null == res) {
					res = new ThreadLocal() {
						protected SimpleDateFormat initialValue() {
							return new SimpleDateFormat(DateFormatter.this.formatStr);
						}
					};
					sdfMap.put(this.formatStr, res);
				}
			}
		}
		return (SimpleDateFormat) res.get();
	}

}
