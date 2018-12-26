package org.platform.modules.bootstrap.handler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.platform.utils.clazz.ObjectMethod;
import org.platform.utils.clazz.ObjectMethodParams;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class UrlMappingStorage {
	
	private static Map<String, ObjectMethod> normalMappings = new HashMap<String, ObjectMethod>();
	private static Map<String, ObjectMethod> restfulMappings = new HashMap<String, ObjectMethod>();
	
	public static Map<String, ObjectMethod> getNormalMappings() {
		return normalMappings;
	}

	public static Map<String, ObjectMethod> getRestfulMappings() {
		return restfulMappings;
	}

	public static class Mapper {
		
		private String[] baseUrl = null;
		private Object controller = null;
		private List<Method> methods = new ArrayList<Method>();

		public Mapper(String[] baseUrl, Object instance) {
			this.baseUrl = baseUrl;
			this.controller = instance;
		}

		public void add(Method method) {
			this.methods.add(method);
		}

		public Object getController() {
			return controller;
		}

		public Map<String, ObjectMethod> getMappings() {
			Map<String, ObjectMethod> mappings = new HashMap<String, ObjectMethod>();
			if (baseUrl == null) {
				baseUrl = new String[] { "" };
			}
			for (String base : baseUrl) {
				for (Method method : methods) {
					RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
					String[] url = requestMapping.value();
					RequestMethod[] methods = requestMapping.method();
					for (String u : url) {
						String path = StringUtils.isNotBlank(u) ? base + "/" + u : base;
						path = path.replaceAll("/+", "/");
						if (methods == null || methods.length == 0) {
							methods = new RequestMethod[] {RequestMethod.POST, RequestMethod.GET};
						}
						for (RequestMethod rm : methods) {
							mappings.put(path + "\b" + rm.toString(), new ObjectMethod(controller, method));
						}
					}
				}
			}
			return mappings;
		}
	}

	public static void addMapper(Mapper mapper) {
		Map<String, ObjectMethod> mappings = mapper.getMappings();
		for (Entry<String, ObjectMethod> entry : mappings.entrySet()) {
			String key = entry.getKey();
			if (key.contains("{") && key.contains("}")) {
				key = key.substring(0, key.indexOf("\b"));
				List<String> names = new ArrayList<String>();
				Pattern pattern = Pattern.compile("\\{[^/]+\\}");
				Matcher matcher = pattern.matcher(key);
				StringBuffer sb = new StringBuffer();
				while (matcher.find()) {
					names.add(matcher.group().replaceAll("\\{([^/]+)\\}", "$1"));
					matcher.appendReplacement(sb, matcher.group().replaceAll("\\{[^/]+\\}", "([^/]+)"));
				}
				matcher.appendTail(sb);
				key = sb.toString() + "\b" + StringUtils.join(names, ",");
				restfulMappings.put(key, entry.getValue());
			} else {
				normalMappings.put(key, entry.getValue());
			}
		}
	}

	public static ObjectMethodParams getObjectMethod(String url) throws UnsupportedEncodingException {
		ObjectMethodParams omp = getObjectMethod(url, "GET");
		return omp != null ? omp : getObjectMethod(url, "POST");
	}

	public static ObjectMethodParams getObjectMethod(String url, String method) throws UnsupportedEncodingException {
		String path = url + "\b" + method;
		ObjectMethod om = normalMappings.get(path);
		if (om != null) {
			return new ObjectMethodParams(om, null);
		} else {
			if (url.matches(".+/")) {
				url = url.replaceAll("/$", "");
				om = normalMappings.get(url + "\b" + method);
			}
			if (om == null && url.matches(".+\\.\\w+")) {
				url = url.replaceAll("\\.\\w+$", "");
				om = normalMappings.get(url + "\b" + method);
			}
			if (om != null) {
				return new ObjectMethodParams(om, null);
			} else {
				TreeMap<Integer, ObjectMethodParams> treeMap = new TreeMap<Integer, ObjectMethodParams>();
				for (Entry<String, ObjectMethod> entry : restfulMappings.entrySet()) {
					String key = entry.getKey();
					String[] names = key.substring(key.indexOf("\b") + 1).split(",");
					key = key.substring(0, key.indexOf("\b"));
					if (url.matches(key)) {
						ObjectMethod value = entry.getValue();
						Map<String, String> params = new HashMap<String, String>();
						for (int i = 0; i < names.length; i++) {
							String tmp = url.replaceAll(key, "$" + (i + 1));
							if(tmp != null && tmp.contains("%")){
								tmp = URLDecoder.decode(tmp,"UTF-8");
							}
							params.put(names[i], tmp);
						}
						treeMap.put(names.length, new ObjectMethodParams(value, params));
					}
				}
				if (treeMap.size() > 0) {
					return treeMap.firstEntry().getValue();
				}
			}
		}
		return null;
	}

}
