package org.platform.modules.login;

public class Constants {
	
	/**
     * 当前登录的用户
     */
    public static final String CURRENT_USER = "user";
    public static final String CURRENT_ACCOUNT = "account";
    public static final String CURRENT_USERNAME = "username";
    public static final String CURRENT_NICKNAME = "nickname";
    
    /**
     * 上个页面地址
     */
    public static final String BACK_URL = "BackURL";

    public static final String IGNORE_BACK_URL = "ignoreBackURL";

    /**
     * 当前请求的地址 带参数
     */
    public static final String CURRENT_URL = "currentURL";

    public static final String CONTEXT_URL = "contextURL";

    /** 编码*/
    public static final String ENCODING = "UTF-8";
    
    /** Session常量*/
    public static final String SESSION_CURRENT_USER = "_CURRENT_USER_";
    public static final String SESSION_CURRENT_USER_ACCOUNT = "_CURRENT_USER_ACCOUNT_";
    public static final String SESSION_CURRENT_USER_RIGHTS = "_CURRENT_USER_RIGHTS_";
    public static final String SESSION_VERIFICATION_CODE = "_VERIFICATION_CODE_";
    
    /** Cookie常量*/
    public static final String COOKIE_USER_ACCOUNT = "_ua_";
    public static final String COOKIE_USER_SESSION = "_session_";
    public static final String COOKIE_USER_ACCESS_TOKEN = "accessToken";
    
}
