package org.platform.modules.bootstrap.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.annotation.ApiV1RestController;
import org.platform.modules.abstr.annotation.ApiV2RestController;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.abstr.web.WebResult;
import org.platform.modules.bootstrap.handler.UrlMappingStorage.Mapper;
import org.platform.modules.bootstrap.service.IHandlerChainService;
import org.platform.utils.clazz.ClassScanner;
import org.platform.utils.clazz.ObjectMethodParams;
import org.platform.utils.clazz.ParameterBinder;
import org.platform.utils.clazz.ClassScanner.ClassResourceHandler;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.spring.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("urlHandlerAdapter")
public class UrlHandlerAdapter implements HandlerAdapter, InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(UrlHandlerAdapter.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	private List<IHandlerChainService> handlerChainServiceList = new ArrayList<IHandlerChainService>();

	public boolean supports(Object handler) {
		return true;
	}

	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}
	
	public Class<?>[] scanAnnotationClasses() {
		return new Class<?>[]{ApiV1RestController.class, ApiV2RestController.class};
	}

	@Override
	public void afterPropertiesSet() {
		Map<String, IHandlerChainService> beans = SpringBeanFactory.getBeansOfType(IHandlerChainService.class);
		for (Map.Entry<String, IHandlerChainService> entry : beans.entrySet()) {
			handlerChainServiceList.add(entry.getValue());
		}
		
		new ClassScanner(new String[] { "org.platform.modules" }, new ClassResourceHandler() {
			
			public void handle(MetadataReader metadataReader) {
				if (metadataReader.getClassMetadata().isAnnotation()) return;
				AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
				Class<?>[] annotationClazzArray = scanAnnotationClasses();
				Class<?> annotationClazz = null;
				boolean includeAnnotationClazz = false;
				for (int i = 0, len = annotationClazzArray.length; i < len; i++) {
					annotationClazz = annotationClazzArray[i];
					if (annotationMetadata.hasAnnotation(annotationClazz.getName())) {
						includeAnnotationClazz = true;
						break;
					}
				}
				if (!includeAnnotationClazz) return;
				RequestMapping requestMapping = annotationClazz.getAnnotation(RequestMapping.class);
				String[] baseUrl = requestMapping.value();
				String className = metadataReader.getClassMetadata().getClassName();
				try {
					Class<?> clazz = Class.forName(className);
					final Mapper mapper = new Mapper(baseUrl, clazz.newInstance());
					ReflectionUtils.doWithMethods(clazz, new MethodCallback() {
						public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
							if (Modifier.isPublic(method.getModifiers())
									&& method.isAnnotationPresent(RequestMapping.class)) {
								mapper.add(method);
							}
						}
					});
					UrlMappingStorage.addMapper(mapper);
					ReflectionUtils.doWithFields(clazz, new FieldCallback() {
						public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
							Resource resource = field.getAnnotation(Resource.class);
							String beanName = null == resource ? field.getName() : resource.name();
							if (SpringBeanFactory.containsBean(beanName) && 
									SpringBeanFactory.isTypeMatch(beanName, field.getType())) {
								ReflectionUtils.makeAccessible(field);
								ReflectionUtils.setField(field, mapper.getController(), SpringBeanFactory.getBean(beanName));
							}
							Autowired autowired = field.getAnnotation(Autowired.class);
							if (null != autowired) {
								ReflectionUtils.makeAccessible(field);
								ReflectionUtils.setField(field, mapper.getController(), SpringBeanFactory.getBean(field.getType()));
							}
						}
					});
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}).scan();
	}

	@Override
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		try {
			String path = request.getServletPath();
			if (path.endsWith("/favicon.ico")) return null;
			LOG.info("url handler adapter request path: {}", path);
			for (IHandlerChainService handlerChainService : handlerChainServiceList) {
				Object[] preResult = handlerChainService.preHandle(request);
				if (preResult.length == 0 || (Boolean) preResult[0])
					continue;
				Object exceptionObj = preResult[1];
				if (exceptionObj instanceof BusinessException) {
					throw (BusinessException) exceptionObj;
				}
			}
			Object result = null;
			if (path.startsWith("/ext/api/")) {
				result = handleExternalRequest(path, request, response);
			} else if (path.startsWith("/api/")) {
				result = handleNormalRequest(path, request, response);
			} else {
				result = handleNormalRequest(path, request, response);
			}
			for (IHandlerChainService handlerChainService : handlerChainServiceList) {
				handlerChainService.postHandle(request, result);
			}
			writeResponse(response, result);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			writeResponse(response, wrapperFailureWebResult(ResultCode.FAILURE, e.getMessage()));
		}
		return null;
	}

	private WebResult wrapperFailureWebResult(int code, String failure) {
		return new WebResult().buildFailure(code, failure);
	}

	private WebResult wrapperFailureWebResult(ResultCode resultCode, String failure) {
		return wrapperFailureWebResult(resultCode.getCode(), failure);
	}

	private Object handleNormalRequest(String path, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		String interfaceUrl = path.replaceAll("/+", "/").replaceAll("^/ext", "");
		ObjectMethodParams omp = UrlMappingStorage.getObjectMethod(interfaceUrl, request.getMethod());
		if (omp == null) {
			return wrapperFailureWebResult(ResultCode.URL_MAPPING_ERROR,
					"No mapping found for HTTP request with URI " + path);
		}
		Method method = omp.getMethod();
		if (method == null) {
			return wrapperFailureWebResult(ResultCode.URL_MAPPING_ERROR,
					"No mapping found for HTTP request with URI " + path);
		}
		try {
			ParameterBinder parameterBinder = new ParameterBinder();
			Object[] params = parameterBinder.bindParameters(omp, omp.getParams(), request, response);
			Object result = ReflectionUtils.invokeMethod(method, omp.getObject(), params);
			return method.getReturnType() == void.class || response.isCommitted() ? null : result;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			if (e.getCause() != null)
				e = (Exception) e.getCause();
			return wrapperFailureWebResult(ResultCode.FAILURE, e.getMessage());
		}
	}

	private Object handleExternalRequest(String interfaceUrl, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		return handleExternalRequest(interfaceUrl, new HashMap<String, String>(), request, response);
	}

	private Object handleExternalRequest(String interfaceUrl, Map<String, String> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		ObjectMethodParams omp = UrlMappingStorage.getObjectMethod(interfaceUrl);
		if (omp == null) {
			return wrapperFailureWebResult(ResultCode.URL_MAPPING_ERROR,
					"No mapping found for HTTP request with URI " + interfaceUrl);
		}
		Method method = omp.getMethod();
		if (method == null) {
			return wrapperFailureWebResult(ResultCode.URL_MAPPING_ERROR,
					"No mapping found for HTTP request with URI " + interfaceUrl);
		}
		try {
			ParameterBinder parameterBinder = new ParameterBinder();
			paramMap.putAll(omp.getParams());
			Object[] params = parameterBinder.bindParameters(omp, paramMap, request, response);
			Object result = ReflectionUtils.invokeMethod(method, omp.getObject(), params);
			return method.getReturnType() == void.class || response.isCommitted() ? "" : result;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			if (e.getCause() != null) {
				e = (Exception) e.getCause();
			}
			return wrapperFailureWebResult(ResultCode.FAILURE, e.getMessage());
		}
	}

	private void writeResponse(HttpServletResponse response, Object result)
			throws JsonGenerationException, JsonMappingException, IOException {
		if (null != result && result instanceof ByteArrayOutputStream) {
			ByteArrayOutputStream baos = (ByteArrayOutputStream) result;
			response.getOutputStream().write(baos.toByteArray());
			baos.close();
		} else if (null == result || StringUtils.isBlank(String.valueOf(result))) {
			
		} else {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			objectMapper.setSerializationInclusion(Include.NON_NULL);
			response.getWriter().write(objectMapper.writeValueAsString(result));
		}
	}

}
