package org.platform.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.List;

import org.platform.modules.login.web.filter.UserAccessFilter;
import org.platform.modules.login.web.filter.UserAuthFilter;
import org.platform.modules.login.web.filter.VerificationCodeFilter;
import org.platform.modules.login.web.filter.WebContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilterConfiguration {

	@Bean
	public FilterRegistrationBean<WebContextFilter> webContextFilter() {
		FilterRegistrationBean<WebContextFilter> filterRegistrationBean = new FilterRegistrationBean<WebContextFilter>();
		filterRegistrationBean.setFilter(new WebContextFilter());
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(2);
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<VerificationCodeFilter> verificationCodeFilter() {
		FilterRegistrationBean<VerificationCodeFilter> filterRegistrationBean = new FilterRegistrationBean<VerificationCodeFilter>();
		filterRegistrationBean.setFilter(new VerificationCodeFilter());
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/verificationCode.jpg");
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(3);
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<UserAccessFilter> userAccessFilter() {
		FilterRegistrationBean<UserAccessFilter> filterRegistrationBean = new FilterRegistrationBean<UserAccessFilter>();
		filterRegistrationBean.setFilter(new UserAccessFilter());
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(4);
		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<UserAuthFilter> userAuthFilter() {
		FilterRegistrationBean<UserAuthFilter> filterRegistrationBean = new FilterRegistrationBean<UserAuthFilter>();
		filterRegistrationBean.setFilter(new UserAuthFilter());
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(5);
		return filterRegistrationBean;
	}

}
