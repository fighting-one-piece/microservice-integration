package org.cisiondata.modules.consumer.feign;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

@Component
public class CacheServiceFactoryImpl implements FallbackFactory<ICacheService> {

	@Override
	public ICacheService create(Throwable cause) {
		return new ICacheService() {
			
			@Override
			public int set(String key, Object value) {
				return 0;
			}
			
			@Override
			public Object get(String key) {
				return "invoke get error!";
			}
		};
	}

}
