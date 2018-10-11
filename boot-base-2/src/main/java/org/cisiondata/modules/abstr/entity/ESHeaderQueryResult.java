package org.cisiondata.modules.abstr.entity;

import java.util.ArrayList;
import java.util.List;

public class ESHeaderQueryResult<Entity> extends ESQueryResult<Entity> {
	
	private static final long serialVersionUID = 1L;
	
	/** Header信息集合*/
	private List<Header> headers = null;
	
	public List<Header> getHeaders() {
		if (null == headers) headers = new ArrayList<Header>();
		return headers;
	}
	
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

}
