package org.cisiondata.modules.bootstrap.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	@Autowired
    private AuthenticationManager authenticationManager = null;
	
	@Autowired
	@Qualifier("masterDataSource")
    private DataSource dataSource = null;
	
	@Autowired
    private RedisConnectionFactory connectionFactory;
	
	@Bean
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}
	
	@Bean
    public RedisTokenStore redisTokenStore() {
        return new RedisTokenStore(connectionFactory);
    }
	
    @Bean 
	public ClientDetailsService clientDetails() {
    	return new JdbcClientDetailsService(dataSource);
	}
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    	security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	endpoints.authenticationManager(authenticationManager);
    	endpoints.tokenStore(redisTokenStore());
    	/**
        endpoints.tokenStore(tokenStore());
		*/

        // 配置TokenServices参数
    	/**
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        tokenServices.setSupportRefreshToken(false);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        tokenServices.setAccessTokenValiditySeconds( (int) TimeUnit.DAYS.toSeconds(30)); // 30天
        endpoints.tokenServices(tokenServices);
  		*/
    }
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
	        .withClient("app_client")
	        .scopes("app") 
	        .secret("app_secret")
	        .authorizedGrantTypes("password", "authorization_code", "refresh_token")
	       .and()
	        .withClient("web_client")
	        .scopes("web")
	        .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit");
		
		/**
		clients.jdbc(dataSource)
			.withClient("client_id")
			.secret("client_secret")
			.scopes("app")
			.authorizedGrantTypes("password", "authorization_code", "refresh_token") // 该client允许的授权类型
			;
		*/
			
		/**
		clients.inMemory() // 使用in-memory存储
				.withClient("client_id") // client_id
				.secret("client_secret") // client_secret
				.authorizedGrantTypes("authorization_code") // 该client允许的授权类型
				.scopes("app"); // 允许的授权范围
		**/
	}

}
