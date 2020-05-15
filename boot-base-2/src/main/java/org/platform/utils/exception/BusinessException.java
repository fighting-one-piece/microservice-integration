package org.platform.utils.exception;

import org.platform.modules.abstr.entity.ResultCode;

/** 业务访问异常*/
public class BusinessException extends GenericException {

	private static final long serialVersionUID = 1L;

	public BusinessException(String module, int code, Object[] args, String defaultMessage) {
		super(module, code, args, defaultMessage);
    }
	
	public BusinessException(int code, String defaultMessage, Object... args) {
    	super(null, code, null, String.format(defaultMessage, args));
    }
	
	public BusinessException(String module, int code, Object[] args) {
        super(module, code, args, null);
    }
	
    public BusinessException(String module, String defaultMessage) {
    	super(module, 0, null, defaultMessage);
    }

    public BusinessException(int code, String defaultMessage) {
    	super(null, code, null, defaultMessage);
    }

    public BusinessException(String defaultMessage) {
    	super(null, 0, null, defaultMessage);
    }
    
    public BusinessException(ResultCode resultCode) {
    	super(null, resultCode.getCode(), null, resultCode.getDesc());
    }
    
    public BusinessException(ResultCode resultCode, Object... args) {
    	super(null, resultCode.getCode(), null, String.format(resultCode.getDesc(), args));
    }

}
