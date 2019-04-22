package org.platform.modules.abstr.web;

public enum ResultCode {

	SUCCESS(1, "操作成功"),
	FAILURE(2, "操作失败"),
	NOT_FOUNT_DATA(3, "未查询到数据"),
	SYSTEM_IS_BUSY(5, "系统繁忙，请稍后再试"),
	QUERY_FAILURE(7, "查询失败"),
	
	PARAM_NULL(-101, "请求参数为空"),
	PARAM_ERROR(-102, "请求参数错误或非法请求"),
	PARAM_FORMAT_ERROR(-103, "请求参数格式错误"),
	
	DATA_EXISTED(-110, "数据已存在"),
	URL_MAPPING_ERROR(-120, "URL Mapping错误"),
	PAGE_NOT_FOUND(-190, "页面不存在"),
	
	DATABASE_CONNECTION_FAIL(-201, "数据库连接失败"),
	DATABASE_READ_FAIL(-202, "数据库读取失败"),
	DATABASE_OPERATION_FAIL(-203, "数据库操作失败"),
	
	INTERFACE_TIMEOUT(1001,"接口超时"),
	INTERFACE_MAINTAIN(1002,"接口维护");
	
	/** 代码值*/
	private int code = 0;
	/** 代码值描述*/
	private String desc = null;
	
	private ResultCode(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
