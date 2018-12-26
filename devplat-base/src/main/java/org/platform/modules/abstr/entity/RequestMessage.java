package org.platform.modules.abstr.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestMessage implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 请求URL*/
	private String url = null;
	/** 请求参数*/
	private Map<String, String> params = null;
	/** 请求属性*/
	private Map<String, Object> attributes = null;
	/** 请求IP*/
	private String ipAddress = null;
	/** 请求账户*/
	private String account = null;
	/** 请求时间 */
	private Date time = null;
	/** 请求返回结果 */
	private Object returnResult = null;
	
	public RequestMessage() {
		
	}
	
	public RequestMessage(String url, Map<String, String> params, String ipAddress, 
			String account, Date time, Object returnResult) {
		super();
		this.url = url;
		this.params = params;
		this.ipAddress = ipAddress;
		this.account = account;
		this.time = time;
		this.returnResult = returnResult;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParams() {
		if (null == params) params = new HashMap<String, String>();
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Map<String, Object> getAttributes() {
		if (null == attributes) attributes = new HashMap<String, Object>();
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Object getReturnResult() {
		return returnResult;
	}

	public void setReturnResult(Object returnResult) {
		this.returnResult = returnResult;
	}

}
