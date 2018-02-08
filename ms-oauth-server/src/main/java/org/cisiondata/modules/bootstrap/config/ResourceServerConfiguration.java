package org.cisiondata.modules.bootstrap.config;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	@Value("${security.oauth2.resource.id}")
    private String resourceId;
	
	@Resource(name = "redisTokenStore")
	private TokenStore redisTokenStore = null;
	
	@Resource(name = "customTokenServices")
    private ResourceServerTokenServices customTokenServices = null;

	@Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceId).tokenServices(customTokenServices).tokenStore(redisTokenStore);
    }
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		/**
		http.antMatcher("/api/v1/**" )
        .authorizeRequests().anyRequest().authenticated()
        .and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		*/
		http.anonymous().disable();
		http.csrf().disable().exceptionHandling()
			.authenticationEntryPoint(
				(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
			.and().authorizeRequests().anyRequest().authenticated()
			.antMatchers(HttpMethod.OPTIONS).permitAll()
//			.antMatchers("/oauth/**").permitAll()
//			.antMatchers("/api/v1/**" ).authenticated()
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().httpBasic();
	}

}
