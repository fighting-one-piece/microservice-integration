package org.platform.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.platform.modules.abstr.web.filter.XssStringJsonSerializer;
import org.platform.modules.bootstrap.filter.XSSFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.alibaba.druid.support.http.StatViewServlet;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	
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
	public FilterRegistrationBean<XSSFilter> xssFilter(){
		FilterRegistrationBean<XSSFilter> filterRegistrationBean = new FilterRegistrationBean<XSSFilter>();
		filterRegistrationBean.setFilter(new XSSFilter());
		filterRegistrationBean.setName("XSSFilter");
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");//拦截路径，可以添加多个
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
	
	@Value("${datasource.master.username}")
	private String username = null;
	
	@Value("${datasource.master.password}")
	private String password = null;
	
	@Bean
	public ServletRegistrationBean<StatViewServlet> statViewServlet(){
		StatViewServlet statViewServlet = new StatViewServlet();
		ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>();
		registrationBean.setServlet(statViewServlet);
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("loginUsername", username);
		initParameters.put("loginPassword", password);
		registrationBean.setInitParameters(initParameters);
		List<String> urlMappings = new ArrayList<String>();
		urlMappings.add("/druid/*");
		registrationBean.setUrlMappings(urlMappings);
		registrationBean.setLoadOnStartup(1);
		return registrationBean;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		return objectMapper;
	}
	
	@Bean
	@Primary
	public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build(); 
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
		xssModule.addSerializer(new XssStringJsonSerializer());
		objectMapper.registerModule(xssModule);
		return objectMapper;
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
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		/**
        SimpleModule simpleModule1 = new SimpleModule();
        simpleModule1.addSerializer(Integer.class, ToStringSerializer.instance);
        simpleModule1.addSerializer(Integer.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule1);
        
        SimpleModule simpleModule2 = new SimpleModule();
        simpleModule2.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule2.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule2);
        
        SimpleModule simpleModule3 = new SimpleModule();
        simpleModule3.addSerializer(Float.class, ToStringSerializer.instance);
        simpleModule3.addSerializer(Float.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule3);
        
        SimpleModule simpleModule4 = new SimpleModule();
        simpleModule4.addSerializer(Double.class, ToStringSerializer.instance);
        simpleModule4.addSerializer(Double.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule4);
		*/
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(0, jackson2HttpMessageConverter);
	}
	
}
