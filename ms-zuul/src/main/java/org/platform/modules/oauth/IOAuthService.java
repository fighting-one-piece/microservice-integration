package org.platform.modules.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import feign.hystrix.FallbackFactory;

//@FeignClient(name = "oauth-server" , fallback = OAuthServiceFallback.class)
@FeignClient(name = "oauth-server", fallbackFactory = OAuthServiceFallbackFactory.class)
public interface IOAuthService {
	
	@RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
	public Map<String, Object> token(@RequestHeader("Authorization") String authorization, 
		@RequestParam("grant_type") String grantType, @RequestParam("username") String username, 
			@RequestParam("password") String password, @RequestParam("refresh_token") String refreshToken);
	
	
	@RequestMapping(value = "/oauth/check_token", method = RequestMethod.GET)
	public Map<String, Object> checkToken(@RequestHeader("Authorization") String authorization, 
		@RequestParam("token") String token);

}

@Component
class OAuthServiceFallback implements IOAuthService {
	
	@Override
	public Map<String, Object> token(String authorization, String grantType, String username, String password,
			String refreshToken) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error", "invalid_grant");
		return result;
	}
	
	@Override
	public Map<String, Object> checkToken(String authorization, String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("error", "invalid_token");
		return result;
	}
	
}

@Component
class OAuthServiceFallbackFactory implements FallbackFactory<IOAuthService> {

	@Override
	public IOAuthService create(Throwable cause) {
		return new IOAuthService() {
			
			@Override
			public Map<String, Object> token(String authorization, String grantType, String username, String password,
					String refreshToken) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("error", "invalid_grant");
				result.put("error_description", cause.getMessage());
				return result;
			}
			
			@Override
			public Map<String, Object> checkToken(String authorization, String token) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("error", "invalid_token");
				result.put("error_description", cause.getMessage());
				return result;
			}
		};
	}
	
}

