package org.platform.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (null == SpringContext.context) {
			SpringContext.context = applicationContext;
		}
	}

	public synchronized static ApplicationContext getApplicationContext() {
		return context;
	}
	
	public static <T> T get(String name, Class<T> requiredType) {
		return getApplicationContext().getBean(name, requiredType);
	}

	public static Object get(String name) {
		return getApplicationContext().getBean(name);
	}

}
