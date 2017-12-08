package org.cisiondata.modules.bootstrap.config;

import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import feign.Response;
import feign.codec.ErrorDecoder;

@Configuration
public class BizExceptionFeignErrorDecoder implements ErrorDecoder{

    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() >= 400 && response.status() <= 499){
            return new HystrixBadRequestException("");
        }
        return feign.FeignException.errorStatus(methodKey, response);
    }

}
