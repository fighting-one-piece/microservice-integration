package org.platform.utils.clazz;

import java.lang.reflect.Method;

public class ObjectMethod {

	private Object object = null;
	private Method method = null;

	public ObjectMethod(Object controller, Method method) {
		this.object = controller;
		this.method = method;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}
	
}
