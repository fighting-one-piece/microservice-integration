package org.platform.utils.exception;

/** 业务访问异常*/
public class BusinessException extends GenericException {

	private static final long serialVersionUID = 1L;

	public BusinessException(String module, int code, Object[] args, String defaultMessage) {
		super(module, code, args, defaultMessage);
    }
	
	public BusinessException(String module, int code, Object[] args) {
        super(module, code, args, null);
    }

    public BusinessException(String module, String defaultMessage) {
    	super(module, 0, null, defaultMessage);
    }

    public BusinessException(int code, Object[] args) {
    	super(null, code, args, null);
    }
    
    public BusinessException(int code, String defaultMessage) {
    	super(null, code, null, defaultMessage);
    }

    public BusinessException(String defaultMessage) {
    	super(null, 0, null, defaultMessage);
    }
    
}
