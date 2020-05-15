package org.platform.modules.abstr.entity;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 结果代码*/
	private Integer code = null;
	/** 结果数据*/
	private Object data = null;
	/** 错误信息*/
	private String failure = null;

	public Result() {}
	
	public Result(Integer code, Object data) {
		this.code = code;
		this.data = data;
	}
	
	public Result(Integer code, Object data, String failure) {
		this.code = code;
		this.data = data;
		this.failure = failure;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getFailure() {
		return failure;
	}

	public void setFailure(String failure) {
		this.failure = failure;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static Result build(ResultCode code) {
		return new Result(code.getCode(), code.getDesc());
	}
	
	public static Result buildSuccess() {
		return build(ResultCode.SUCCESS);
	}
	
	public static Result buildSuccess(Object data) {
		return new Result(ResultCode.SUCCESS.getCode(), data);
	}
	
	public static Result buildFailure(int code, String failure) {
		return new Result(code, null, failure);
	}

}
