package org.platform.modules.bootstrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;

@Configuration
public class FeignConfiguration {

	@Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
	
	/**
	@Bean
	public Contract feignContract() {
		return new Contract.Default();
	}
	*/

	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor("user", "password");
	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
	
	/**
	HttpMessageConverters httpMessageConverters = new HttpMessageConverters();

	ObjectFactory<HttpMessageConverters> messageConvertersObjectFactory = new ObjectFactory<HttpMessageConverters>() {
		@Override
		public HttpMessageConverters getObject() throws BeansException {
			return httpMessageConverters;
		}
	};
	*/

	/**
	public Encoder feignEncoder() {
		return new SpringEncoder(messageConvertersObjectFactory);
	}
	*/

}
