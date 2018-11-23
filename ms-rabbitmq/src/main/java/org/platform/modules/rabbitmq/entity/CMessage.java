package org.platform.modules.rabbitmq.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;

	// 交换器
	private String exchange = null;
	// 路由key
	private String routeKey = null;
	// 参数类型
	private Class<?>[] paramTypes = null;
	// 参数
	private Object[] params = null;

	public CMessage() {
	}

	public CMessage(String exchange, String routeKey, Object... params) {
		this.params = params;
		this.exchange = exchange;
		this.routeKey = routeKey;
	}

	@SuppressWarnings("rawtypes")
	public CMessage(String exchange, String routeKey, String methodName, Object... params) {
		this.exchange = exchange;
		this.routeKey = routeKey;
		this.params = params;
		int len = params.length;
		Class[] clazzArray = new Class[len];
		for (int i = 0; i < len; i++)
			clazzArray[i] = params[i].getClass();
		this.paramTypes = clazzArray;
	}

	public byte[] getSerialBytes() {
		byte[] res = new byte[0];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.close();
			res = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public String getRouteKey() {
		return routeKey;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public void setRouteKey(String routeKey) {
		this.routeKey = routeKey;
	}

	public Class<?>[] getParamTypes() {
		return paramTypes;
	}

	public Object[] getParams() {
		return params;
	}

}
