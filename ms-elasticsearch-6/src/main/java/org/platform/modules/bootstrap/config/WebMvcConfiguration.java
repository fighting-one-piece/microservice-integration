package org.platform.modules.bootstrap.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

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
