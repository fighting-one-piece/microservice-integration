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
		
		private String[] baseUrls = null;
		private Object controller = null;
		private List<Method> methods = new ArrayList<Method>();

		public Mapper(String[] baseUrl, Object instance) {
			this.baseUrls = baseUrl;
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
			if (baseUrls == null) {
				baseUrls = new String[] { "" };
			}
			for (String baseUrl : baseUrls) {
				for (Method method : methods) {
					RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
					RequestMethod[] requestMethods = requestMapping.method();
					String[] requestUrls = requestMapping.value();
					for (String requestUrl : requestUrls) { 
						String path = StringUtils.isNotBlank(requestUrl) ? baseUrl + "/" + requestUrl : baseUrl;
						path = path.replaceAll("/+", "/");
						if (requestMethods == null || requestMethods.length == 0) {
							requestMethods = new RequestMethod[] {RequestMethod.POST, RequestMethod.GET};
						}
						for (RequestMethod requestMethod : requestMethods) {
							mappings.put(path + "\b" + requestMethod.toString(), new ObjectMethod(controller, method));
						}
					}
				}
			}
			return mappings;
		}
	}
	
	private static Pattern pattern = Pattern.compile("\\{[^/]+\\}");

	public static void addMapper(Mapper mapper) {
		Map<String, ObjectMethod> mappings = mapper.getMappings();
		for (Entry<String, ObjectMethod> entry : mappings.entrySet()) {
			String path = entry.getKey();
			if (path.contains("{") && path.contains("}")) {
				path = path.substring(0, path.indexOf("\b"));
				List<String> names = new ArrayList<String>();
				Matcher matcher = pattern.matcher(path);
				StringBuffer sb = new StringBuffer();
				while (matcher.find()) {
					names.add(matcher.group().replaceAll("\\{([^/]+)\\}", "$1"));
					matcher.appendReplacement(sb, matcher.group().replaceAll("\\{[^/]+\\}", "([^/]+)"));
				}
				matcher.appendTail(sb);
				path = sb.toString() + "\b" + StringUtils.join(names, ",");
				restfulMappings.put(path, entry.getValue());
			} else {
				normalMappings.put(path, entry.getValue());
			}
		}
	}

	public static ObjectMethodParams getObjectMethod(String url) throws UnsupportedEncodingException {
		ObjectMethodParams omp = getObjectMethod(url, "GET");
		return omp != null ? omp : getObjectMethod(url, "POST");
	}

	public static ObjectMethodParams getObjectMethod(String url, String method) throws UnsupportedEncodingException {
		ObjectMethod om = normalMappings.get(url + "\b" + method);
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
					String path = entry.getKey();
					String[] pathParamNames = path.substring(path.indexOf("\b") + 1).split(",");
					path = path.substring(0, path.indexOf("\b"));
					if (url.matches(path)) {
						ObjectMethod objectMethod = entry.getValue();
						Map<String, String> pathParamMap = new HashMap<String, String>();
						for (int i = 0; i < pathParamNames.length; i++) {
							String pathActualParam = url.replaceAll(path, "$" + (i + 1));
							if(pathActualParam != null && pathActualParam.contains("%")){
								pathActualParam = URLDecoder.decode(pathActualParam,"UTF-8");
							}
							pathParamMap.put(pathParamNames[i], pathActualParam);
						}
						treeMap.put(pathParamNames.length, new ObjectMethodParams(objectMethod, pathParamMap));
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
