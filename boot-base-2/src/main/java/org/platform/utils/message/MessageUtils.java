package org.platform.utils.message;

import java.util.Locale;

import org.platform.utils.spring.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

public class MessageUtils {

	private Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

    private MessageSource messageSource = null;
    
    private MessageUtils(){
    	if (null == messageSource) {
        	messageSource = SpringBeanFactory.getBean("messageSource");
        }
        if (null == messageSource) {
        	messageSource = SpringBeanFactory.getBean(MessageSource.class);
        }
    }
    
    private static class MessageUtilsHolder {
    	private static final MessageUtils INSTANCE = new MessageUtils();
    }
    
    public static final MessageUtils getInstance() {
    	return MessageUtilsHolder.INSTANCE;
    }
    
    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public String getMessage(String code) {
        try {
        	return messageSource.getMessage(code, null, Locale.CHINESE);
        } catch (NoSuchMessageException e) {
        	LOG.error(e.getMessage());
        	return code;
        }
    }
    
    /**
     * 根据消息键和参数 获取消息
     * 委托给spring messageSource
     * @param code 消息键
     * @param args 参数
     * @return
     */
    public String getMessage(String code, Object... args) {
        try {
        	return messageSource.getMessage(code, args, Locale.CHINESE);
        } catch (NoSuchMessageException e) {
        	LOG.error(e.getMessage());
        	return null;
        }
    }

}
