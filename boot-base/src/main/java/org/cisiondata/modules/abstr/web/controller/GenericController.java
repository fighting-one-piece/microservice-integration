package org.cisiondata.modules.abstr.web.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.abstr.service.IGenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;

public abstract class GenericController<Entity extends Serializable, PK extends Serializable> {

	/** 日志 */
	protected Logger LOG = LoggerFactory.getLogger(getClass());

	protected Class<Entity> entityClass = null;

	private String viewPrefix = null;

	@SuppressWarnings("unchecked")
	public GenericController() {
		Type type = getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			entityClass = (Class<Entity>) ((ParameterizedType) type)
					.getActualTypeArguments()[0];
		}
		setViewPrefix(defaultViewPrefix());
	}

	public abstract IGenericService<Entity, PK> obtainServiceInstance();

	protected Entity newEntity() {
		try {
			return entityClass.newInstance();
		} catch (Exception e) {
			LOG.error("can not instantiated entity : {} message: {}", this.entityClass, e.getMessage());
		}
		return null;
	}

	protected String defaultViewPrefix() {
		String currentViewPrefix = "";
		RequestMapping requestMapping = AnnotationUtils.findAnnotation(
				getClass(), RequestMapping.class);
		if (null != requestMapping && requestMapping.value().length > 0) {
			currentViewPrefix = requestMapping.value()[0];
		}
		if (StringUtils.isEmpty(currentViewPrefix)) {
			currentViewPrefix = this.entityClass.getSimpleName();
		}
		return currentViewPrefix;
	}

	/**
	 * @param backURL null 将重定向到默认getViewPrefix()
	 * @return
	 */
	protected String redirectToUrl(String backURL) {
		if (StringUtils.isEmpty(backURL))
			backURL = getViewPrefix();
		if (!backURL.startsWith("/") && !backURL.startsWith("http")) {
			backURL = "/" + backURL;
		}
		return "redirect:" + backURL;
	}

	/**
	 * 当前模块 视图的前缀 默认 1、获取当前类头上的@RequestMapping中的value作为前缀 2、如果没有就使用当前模型小写的简单类名
	 */
	public void setViewPrefix(String viewPrefix) {
		if (viewPrefix.startsWith("/")) viewPrefix = viewPrefix.substring(1);
		this.viewPrefix = viewPrefix;
	}

	public String getViewPrefix() {
		return viewPrefix;
	}

}
