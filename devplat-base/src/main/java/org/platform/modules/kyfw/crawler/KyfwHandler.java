package org.platform.modules.kyfw.crawler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class KyfwHandler {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected KyfwHandler nextHandler = null;
	
	public void handleChain(Map<String, String> params) {
		Map<String, String> result =handle(params);
		if (null != nextHandler) {
			nextHandler.handleChain(result);
		}
	}
	
	public abstract Map<String, String> handle(Map<String, String> params);

	public void setNextHandler(KyfwHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	protected String buildCookie(Map<String, String> params) {
		StringBuilder sb = new StringBuilder(150);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	protected void print(Map<String, ?> map) {
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			System.err.println(entry.getKey() + " : " + entry.getValue());
		}
	}
	
}
