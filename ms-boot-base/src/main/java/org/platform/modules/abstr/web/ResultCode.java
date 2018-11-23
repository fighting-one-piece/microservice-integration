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
	
	VERIFICATION_SUCCESS(200, "验证通过成功返回"),
	VERIFICATION_FAIL(400, "验证失败，cookie或token值无效"),
	VERIFICATION_URL_ERROR(401, "URL语法错误或参数值非法，或access_token无效"),
	VERIFICATION_NO_IP_PERIMISSION(410, "服务器没有开通接口的IP权限"),
	VERIFICATION_NO_EXIST(420, "用户不存在"),
	VERIFICATION_ID_INVALID(427, "id参数无效，需要重新初始化id"),
	VERIFICATION_USER_FAIL(428, "用户认证失败,请重新登录"),
	
	SERVER_ERROR(500, "服务器端错误"),
	SERVER_UNDER_MAINTENANCE(503, "服务器正在维护"),
	
	ACCOUNT_NOT_EXIST(540, "账号不存在"),
	ACCOUNT_PASSWORD_NOT_MATCH(550, "账号密码不匹配"),
	ACCOUNT_EXPIRED_OR_DELETED(551, "账户已过期或已删除"),
	
	SECURITY_ANSWER_ERROR(560, "密保答案错误"),
	
	VERIFICATION_CODE_FAILURE(602, "验证码校验失败");
	
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
