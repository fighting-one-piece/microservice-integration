package org.platform.utils.clazz;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.platform.utils.date.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;

@SuppressWarnings("unchecked")
public class ParameterBinder {

	private Logger LOG = LoggerFactory.getLogger(ParameterBinder.class);
	
	private static final MultipartResolver MULTIPARTRESOLVER = new CommonsMultipartResolver();
	private static Map<String, Object> classMethodParamNames = new HashMap<String, Object>();

	private String getParam(Map<String, String> params, HttpServletRequest request, String name) {
		if (params != null && params.get(name) != null) {
			return params.get(name);
		} else {
			String[] arr = request.getParameterValues(name);
			if (arr != null) {
				return StringUtils.join(arr, "\b");
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Object[] bindParameters(ObjectMethodParams omp, Map<String, String> params, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (MULTIPARTRESOLVER.isMultipart(request)) {
			request = MULTIPARTRESOLVER.resolveMultipart(request);
		}
		Method method = omp.getMethod();
		List<String> parameterNames = this.parseMethodParamNames(method);
		Class[] parameterTypes = method.getParameterTypes();
		Class[] parameterAnnotationTypes = getMethodParamsAnnotationType(method);
		Object paramTarget[] = new Object[parameterTypes.length];
		for (int i = 0, iLen = parameterTypes.length; i < iLen; i++) {
			Class typeClazz = parameterTypes[i];
			Class annotaionTypeClazz = parameterAnnotationTypes[i];
			if (null != annotaionTypeClazz && RequestBody.class.isAssignableFrom(annotaionTypeClazz)) {
				String requestBody = extractRequestBody(request);
				paramTarget[i] = new Gson().fromJson(requestBody, typeClazz);
				continue;
			}
			if (typeClazz.isArray()) {
				String arrayString = getParam(params, request, parameterNames.get(i));
				String[] value = null;
				if (arrayString == null) {
					value = new String[0];
				} else {
					if (arrayString.contains("\b") || !arrayString.matches("\\s*\\[.*\\]\\s*")) {
						value = arrayString.split("\b");
					} else {
						JSONArray ja = JSONArray.parseArray(arrayString);
						value = new String[ja.size()];
						for (int j = 0; j < ja.size(); j++) {
							value[j] = ja.getString(j);
						}
					}
				}
				Object newArray = Array.newInstance(typeClazz.getComponentType(), value.length);
				for (int j = 0, jLen = value.length; j < jLen; j++) {
					try {
						Array.set(newArray, j, BeanUtil.directConvert(value[j], typeClazz.getComponentType()));
					} catch (Exception e) {
					}
				}
				paramTarget[i] = newArray;
			} else if (typeClazz.isPrimitive() || typeClazz == String.class || Number.class.isAssignableFrom(typeClazz)||Boolean.class==typeClazz) {
				paramTarget[i] = BeanUtil.directConvert(getParam(params, request, parameterNames.get(i)), typeClazz);
			} else if (typeClazz == Date.class) {
				String parameterName = parameterNames.get(i);
				if (parameterName.indexOf("\b") != -1) {
					parameterName = parameterName.substring(0, parameterName.indexOf("\b"));
					//String pattern = parameterName.substring(parameterName.indexOf("\b") + 1);
				}
				String dateString = getParam(params, request, parameterName);
				if (StringUtils.isNotBlank(dateString)) {
					Matcher matcher = Pattern.compile("^\\d{1,}$").matcher(dateString);
					if (matcher.matches()) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(Long.parseLong(dateString));
						paramTarget[i] = calendar.getTime();
					} else {
						paramTarget[i] = dateString.indexOf(":") == -1 
								? DateFormatter.DATE.get().parse(dateString)
										: DateFormatter.TIME.get().parse(dateString);
					}
				}
			} else if (ServletRequest.class.isAssignableFrom(typeClazz)) {
				paramTarget[i] = request;
			} else if (ServletResponse.class.isAssignableFrom(typeClazz)) {
				paramTarget[i] = response;
			} else if (HttpSession.class.isAssignableFrom(typeClazz)) {
				paramTarget[i] = request.getSession(false);
			} else if (HttpHeaders.class.isAssignableFrom(typeClazz)) {
				HttpHeaders headers = new HttpHeaders();
				Enumeration enumeration = request.getHeaderNames();
				while (enumeration.hasMoreElements()) {
					String name = (String) enumeration.nextElement();
					List<String> values = new LinkedList<String>();
					Enumeration ve = request.getHeaders(name);
					while (ve.hasMoreElements()) {
						values.add((String) ve.nextElement());
					}
					headers.put(name, values);
				}
				paramTarget[i] = headers;
			} else if (MultipartFile.class.isAssignableFrom(typeClazz)) {
				String fileName = parameterNames.get(i);
				paramTarget[i] = ((MultipartHttpServletRequest) request).getFile(fileName);
			} else {
				String value = getParam(params, request, parameterNames.get(i));
				if (StringUtils.isNotBlank(value)) {
					//兼容泛型 
					Class actualClass = getGenericClass(omp.getObject(), omp.getMethod(), i);
					paramTarget[i] = BeanUtil.directConvert(value, actualClass);
				} else {
					paramTarget[i] = getEntityParameter(typeClazz, parameterNames.get(i), params, request);
				}
			}
		}
		return paramTarget;
	}

	private static <T> Class<T> getClassByType(Type type) {
		if (type instanceof ParameterizedType) {
			Type t1 = ((ParameterizedType) type).getRawType();
			return getClassByType(t1);
		} else if (type instanceof Class) {
			return (Class<T>) type;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private static <T> Class<T> getGenericClass(Object obj, Method method, int index) {
		Class[] actualClassArr = null;
		Map<String, Integer> genericStringMap = new HashMap<String, Integer>();
		Type superType = obj.getClass().getGenericSuperclass();
		if (superType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) superType;
			Type[] arr = pt.getActualTypeArguments();
			actualClassArr = new Class[arr.length];
			for (int i = 0; i < arr.length; i++) {
				actualClassArr[i] = getClassByType(arr[i]);
			}
			TypeVariable[] genericTypeArr = obj.getClass().getSuperclass().getTypeParameters();
			for (int i = 0; i < genericTypeArr.length; i++) {
				genericStringMap.put(genericTypeArr[i].toString(), i);
			}
			Type paramType = method.getGenericParameterTypes()[index];
			if (paramType instanceof Class) {
				return (Class<T>) paramType;
			} else {
				String genericParamString = paramType.toString();
				Integer pos = genericStringMap.get(genericParamString);
				if (pos >= 0 && pos < actualClassArr.length) {
					return actualClassArr[pos];
				}
			}
		}
		return null;
	}

	/**
	 * 从request中获取参数，并转换成目标类型
	 * @param type 目标类型
	 * @param bind 参数信息
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	public Object getEntityParameter(Class type, String name, Map<String, String> params, HttpServletRequest request)
			throws UnsupportedEncodingException {
		Object obj = null;
		try {
			obj = type.newInstance();
		} catch (InstantiationException e) {
			LOG.debug("InstantiationException happened when initializing bussiness object", e);
			return null;
		} catch (IllegalAccessException e) {
			LOG.debug("IllegalAccessException happened when initializing bussiness object", e);
			return null;
		}
		Map<String, String[]> paramMap = request.getParameterMap();
		Map map = new HashMap();
		for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
			String[] array = entry.getValue();
			map.put(entry.getKey(), array.length == 1 ? array[0] : array);
		}
		map.putAll(params);
		BeanUtil.copyProperties(map, obj);
		return obj;
	}

	public List<String> parseMethodParamNames(Method method) throws Exception {
		String clazz = method.getDeclaringClass().getName();
		Map<String, List<String>> names = (Map<String, List<String>>) classMethodParamNames.get(clazz);
		if (names == null) {
			names = new ParameterNameParser().parse(clazz);
			classMethodParamNames.put(clazz, names);
		}
		return names.get(getMethodLongName(method));
	}
	
	private static String getMethodLongName(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName()).append("(");
		for(Class<?> type : method.getParameterTypes()){
			sb.append(type.getTypeName()).append(",");
		}
		if(method.getParameterTypes().length>0) sb.delete(sb.length()-1, sb.length());
		sb.append(")");
		return sb.toString();
	}
	
	private Class<?>[] getMethodParamsAnnotationType(Method method) {
		Annotation[][] annotationss = method.getParameterAnnotations();
		Class<?>[] annotationTypes = new Class[annotationss.length];
		for (int i = 0, len = annotationss.length; i < len; i++) {
			Annotation[] annotations = annotationss[i];
			if (annotations.length == 0) {
				annotationTypes[i] = null;
			} else if (annotations.length == 1) {
				annotationTypes[i] = annotations[0].annotationType();
			}
		}
		return annotationTypes;
	}
	
	private String extractRequestBody(HttpServletRequest request) {
		String charset = request.getCharacterEncoding();   
		if (charset == null)  charset = "UTF-8";   
		CharArrayWriter bodyData = new CharArrayWriter();   
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));
			char[] buf = new char[8192];   
			int length;   
			while ((length = br.read(buf, 0, 8192)) != -1) {   
				bodyData.write(buf, 0, length);   
			}   
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != br) br.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return bodyData.toString();
	}

}
