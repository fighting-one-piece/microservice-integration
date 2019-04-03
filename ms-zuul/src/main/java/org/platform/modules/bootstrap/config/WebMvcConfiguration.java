package org.platform.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.platform.modules.filter.AccessFilter;
import org.platform.modules.filter.CustomFilterProcessor;
import org.platform.modules.filter.ExtErrorFilter;
import org.platform.modules.interceptor.LoggerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	@Autowired
	private LoggerInterceptor loggerInterceptor = null;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggerInterceptor).addPathPatterns("/**");
	}
	
	@Bean
	public CustomFilterProcessor CustomFilterProcessor() {
		return new CustomFilterProcessor();
	}
	
	@Bean
	public AccessFilter accessFilter() {
		return new AccessFilter();
	}
	
	@Bean
	public ExtErrorFilter extErrorFilter() {
		return new ExtErrorFilter();
	}
	
	@Bean
	public ServletListenerRegistrationBean<EventListener> requestContextListener(){
		ServletListenerRegistrationBean<EventListener> listenerRegistrationBean
			= new ServletListenerRegistrationBean<EventListener>();
		listenerRegistrationBean.setListener(new RequestContextListener());
//		registrationBean.setOrder(1);
		return listenerRegistrationBean;
	}
	
	@Bean
	public ServletListenerRegistrationBean<EventListener> introspectorCleanupListener(){
		ServletListenerRegistrationBean<EventListener> listenerRegistrationBean
			= new ServletListenerRegistrationBean<EventListener>();
		listenerRegistrationBean.setListener(new IntrospectorCleanupListener());
		return listenerRegistrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter(){
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		FilterRegistrationBean<CharacterEncodingFilter> filterRegistrationBean = new FilterRegistrationBean<CharacterEncodingFilter>();
		filterRegistrationBean.setFilter(characterEncodingFilter);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");//拦截路径，可以添加多个
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
	
	@Bean
	public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter(){
		FilterRegistrationBean<HiddenHttpMethodFilter> filterRegistrationBean = new FilterRegistrationBean<HiddenHttpMethodFilter>();
		filterRegistrationBean.setFilter(new HiddenHttpMethodFilter());
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");//拦截路径，可以添加多个
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
	
	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
		return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
			@Override
			public void customize(ConfigurableWebServerFactory factory) {
				factory.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400.html"));            
				factory.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/error/401.html"));            
				factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404.html"));        
				factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500.html"));        
			}
		};
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}
	
}
