package org.platform.modules.system.service.impl;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.system.service.IEmailService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.spring.SpringBeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("emailService")
public class EmailServiceImpl implements IEmailService, InitializingBean {

	private JavaMailSender mailSender = null;
	
	@Value("${spring.mail.username:null}")
	private String username = null;
	
	@Value("${spring.mail.host:null}")
	private String host = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isBlank(host) || "null".equals(host)) return;
		mailSender = SpringBeanFactory.getBean(JavaMailSender.class);
	}

	@Override
	public void send(String subject, String text, String... to) throws BusinessException {
		send(username, subject, text, to);
	}
	
	@Override
	public void send(String from, String subject, String text, String... to) throws BusinessException {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(from);
		mailMessage.setTo(to);
		mailMessage.setBcc(to);
		mailMessage.setSubject(subject);
		mailMessage.setText(text);
		mailMessage.setSentDate(Calendar.getInstance().getTime());
		mailSender.send(mailMessage);
	}
	
}
