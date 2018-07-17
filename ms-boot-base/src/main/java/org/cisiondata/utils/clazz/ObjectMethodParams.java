package org.cisiondata.utils.clazz;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectMethodParams {

	private Object object = null;
	private Method method = null;
	private Map<String, String> params = null;

	public ObjectMethodParams(ObjectMethod objectMethod, Map<String, String> params) {
		this.object = objectMethod.getObject();
		this.method = objectMethod.getMethod();
		this.params = params;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}

	public Map<String, String> getParams() {
		return null == params ? new HashMap<String, String>() : params;
	}
}
