package org.platform.modules.oauth.token;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.platform.utils.date.DateFormatter;
import org.platform.utils.token.TokenUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

public class CustomTokenEnhancer implements TokenEnhancer {

	private Calendar calendar = Calendar.getInstance();
	
	private static SimpleDateFormat DDF = DateFormatter.DATE.get();
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			DefaultOAuth2AccessToken defaultAccessToken = (DefaultOAuth2AccessToken) accessToken;
			Object userObj = authentication.getPrincipal();
			if (null != userObj) {
				User user = (User) userObj;
				defaultAccessToken.setValue(TokenUtils.genAuthenticationMD5Token(user.getUsername(), 
					user.getPassword(), DDF.format(calendar.getTime())));
			}
			return defaultAccessToken;
		}
		return accessToken;
	}

}
