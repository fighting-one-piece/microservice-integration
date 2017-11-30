package org.cisiondata.utils.exception;

/** 数据访问异常*/
public class DataException extends GenericException {

	private static final long serialVersionUID = 1L;

	public DataException(String module, int code, Object[] args, String defaultMessage) {
		super(module, code, args, defaultMessage);
    }
	
	public DataException(String module, int code, Object[] args) {
        super(module, code, args, null);
    }

    public DataException(String module, String defaultMessage) {
    	super(module, 0, null, defaultMessage);
    }

    public DataException(int code, Object[] args) {
    	super(null, code, args, null);
    }
    
    public DataException(int code, String defaultMessage) {
    	super(null, code, null, defaultMessage);
    }

    public DataException(String defaultMessage) {
    	super(null, 0, null, defaultMessage);
    }

}
