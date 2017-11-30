package org.cisiondata.modules.bootstrap.config;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.cisiondata.modules.bootstrap.filter.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
	
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
	public FilterRegistrationBean characterEncodingFilter(){
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(characterEncodingFilter);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");//拦截路径，可以添加多个
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
	
	@Bean
	public FilterRegistrationBean hiddenHttpMethodFilter(){
		HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter();
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(hiddenHttpMethodFilter);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");//拦截路径，可以添加多个
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
	
	@Bean
	public FilterRegistrationBean xssFilter(){
		XSSFilter xssFilter = new XSSFilter();
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(xssFilter);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/*");//拦截路径，可以添加多个
		filterRegistrationBean.setUrlPatterns(urlPatterns);
		filterRegistrationBean.setOrder(1);
		return filterRegistrationBean;
	}
	
	/**
	@Bean
	public ServletRegistrationBean demoServlet(){
		DemoServlet demoServlet = new DemoServlet();
		ServletRegistrationBean registrationBean = new ServletRegistrationBean();
		registrationBean.setServlet(demoServlet);
		List<String> urlMappings = new ArrayList<String>();
		urlMappings.add("/demoservlet");////访问，可以添加多个
		registrationBean.setUrlMappings(urlMappings);
		registrationBean.setLoadOnStartup(1);
		return registrationBean;
	}
	**/
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        //不显示为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
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

        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        
        converters.add(0, jackson2HttpMessageConverter);
	}
	
}
