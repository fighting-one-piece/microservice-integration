package org.cisiondata.modules.abstr.entity;

import java.util.ArrayList;
import java.util.List;

public class HeaderQueryResult<Entity> extends QueryResult<Entity> {

	private static final long serialVersionUID = 1L;
	
	/** Header信息集合*/
	private List<Header> headers = null;
	/** 数据结果*/
	private Entity result = null;
	
	public List<Header> getHeaders() {
		if (null == headers) headers = new ArrayList<Header>();
		return headers;
	}
	
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	
	public Entity getResult() {
		return result;
	}
	
	public void setResult(Entity result) {
		this.result = result;
	}

}
