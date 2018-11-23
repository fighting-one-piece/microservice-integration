package org.platform.modules.bootstrap.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Encoder;

@Configuration
public class FeignConfiguration {

	@Bean
    @Scope("prototype")
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
	
	@Bean
	public Contract feignContract() {
		return new Contract.Default();
	}

	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
		return new BasicAuthRequestInterceptor("user", "password");
	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
	
	HttpMessageConverters httpMessageConverters = new HttpMessageConverters();

	ObjectFactory<HttpMessageConverters> messageConvertersObjectFactory = new ObjectFactory<HttpMessageConverters>() {
		@Override
		public HttpMessageConverters getObject() throws BeansException {
			return httpMessageConverters;
		}
	};

	public Encoder feignEncoder() {
		return new SpringEncoder(messageConvertersObjectFactory);
	}

}
