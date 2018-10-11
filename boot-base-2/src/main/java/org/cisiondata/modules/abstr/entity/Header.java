package org.cisiondata.modules.abstr.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Header implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 域英文名称*/
	private String fieldEN = null;
	/** 域中文名称*/
	private String fieldCH = null;
	/** 子域*/
	private List<Header> children = null;
	
	public Header() {}
	
	public Header(String fieldEN, String fieldCH) {
		this.fieldEN = fieldEN;
		this.fieldCH = fieldCH;
	}

	public String getFieldEN() {
		return fieldEN;
	}
	
	public void setFieldEN(String fieldEN) {
		this.fieldEN = fieldEN;
	}
	
	public String getFieldCH() {
		return fieldCH;
	}
	
	public void setFieldCH(String fieldCH) {
		this.fieldCH = fieldCH;
	}
	
	public List<Header> getChildren() {
		return children;
	}
	
	public void setChildren(List<Header> children) {
		this.children = children;
	}
	
	public void addChild(String fieldEN, String fieldCH) {
		if (null == children) children = new ArrayList<Header>();
		children.add(new Header(fieldEN, fieldCH));
	}
	
	public void addChild(Header... headers) {
		if (null == headers || headers.length == 0) {
			throw new RuntimeException("headers error!");
		}
		if (null == children) children = new ArrayList<Header>();
		for (int i = 0, len = headers.length; i < len; i++) {
			children.add(headers[i]);
		}
	}
	
	public void addChild(String... fields) {
		if (null == fields || fields.length == 0 || fields.length % 2 != 0) {
			throw new RuntimeException("fields error!");
		}
		if (null == children) children = new ArrayList<Header>();
		for (int i = 0, len = fields.length; i < len; i++) {
			children.add(new Header(fields[i], fields[++i]));
		}
	}
	
}
