package org.cisiondata.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtils.class);
	
	public static Properties newInstance(String src) {
		InputStream in = null;
		try {
			in = PropertiesUtils.class.getClassLoader().getResourceAsStream(src);
			if (null == in) {
				in = new FileInputStream(new File(src));
			}
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} 
		return newInstance(in);
	}
	
	public static Properties newInstance(InputStream in) {
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != in) in.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return properties;
	}
	
	public static boolean containsKey(String src, Object key) {
		return newInstance(src).containsKey(key);
	}
	
	public static boolean containsValue(String src, Object value) {
		return newInstance(src).containsValue(value);
	}
	
	public static String getProperty(String src, String key) {
		return newInstance(src).getProperty(key);
	}

	public static String getProperty(String src, String key, String defaultValue) {
		return newInstance(src).getProperty(key, defaultValue);
	}
	
	public static Collection<Object> values(String src) {
		return newInstance(src).values();
	}
	
	public static Set<Entry<Object,Object>> entrySet(String src) {
		return newInstance(src).entrySet();
	}

}
