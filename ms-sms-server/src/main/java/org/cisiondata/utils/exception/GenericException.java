package org.cisiondata.utils.exception;

/** 基础异常 */
public class GenericException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/** 所属模块 */
    private String module = null;
    /** 错误码 */
    private int code = 0;
    /** 错误码对应的参数 */
    private Object[] args = null;
    /** 错误消息 */
    private String defaultMessage = null;

    public GenericException(String module, int code, Object[] args, String defaultMessage) {
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

    public GenericException(String module, int code, Object[] args) {
        this(module, code, args, null);
    }

    public GenericException(String module, String defaultMessage) {
        this(module, 0, null, defaultMessage);
    }

    public GenericException(int code, Object[] args) {
        this(null, code, args, null);
    }
    
    public GenericException(int code, String defaultMessage) {
        this(null, code, null, defaultMessage);
    }

    public GenericException(String defaultMessage) {
        this(null, 0, null, defaultMessage);
    }

    public String getModule() {
        return module;
    }

    public int getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public String toString() {
        return this.getClass() + "{module='" + module + '\'' +", message='" + getMessage() + '\'' + '}';
    }
}
