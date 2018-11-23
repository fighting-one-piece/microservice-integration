package org.platform.modules.abstr.entity;

import java.io.Serializable;
import java.util.Date;

public class QueryEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	// 查询URL
	private String url = null;
	// 查询关键字
	private String keyword = null;
	// 查询结果
	private String result = null;
	// 查询时间
	private Date time = null;
	// 表名
	private String tableName = null;
	// 类型
	private String type = null;
	// 正常或错误标记 1、正常 2、错误
	private int flag = 1;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	

}
