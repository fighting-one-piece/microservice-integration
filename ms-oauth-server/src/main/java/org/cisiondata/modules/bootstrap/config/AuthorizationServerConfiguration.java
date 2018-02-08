package org.cisiondata.modules.bootstrap.config;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.cisiondata.modules.oauth.token.CustomTokenEnhancer;
import org.cisiondata.modules.oauth.token.CustomTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	public static final int accessTokenValiditySeconds = 1800;
    public static final int refreshTokenValiditySeconds = 3600;
	
    @Value("${security.oauth2.resource.id}")
	private String resourceId = null;
    
	@Resource(name = "masterDataSource")
    private DataSource dataSource = null;
	
	@Resource(name = "clientDetailsExtService")
	private ClientDetailsService clientDetailsService = null;
	
	@Autowired
    private RedisConnectionFactory redisConnectionFactory = null;
	
	@Autowired
    private AuthenticationManager authenticationManager = null;
	
	@Bean
	public TokenStore inMemoryTokenStore() {
		return new InMemoryTokenStore();
	}
	
	@Bean
	public TokenStore jdbcTokenStore() {
		return new JdbcTokenStore(dataSource);
	}
	
	@Bean
    @Primary
	public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }
	
	@Bean
    public TokenStore JwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
	
	@Bean
	public TokenEnhancer customTokenEnhancer() {
		return new CustomTokenEnhancer();
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	    KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
	    		new ClassPathResource("keystore.jks"), "mypass".toCharArray());
	        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mykeys"));
	    return converter;
	}
	
	/**
    @Bean 
	public ClientDetailsService clientDetailsService() {
    	return new JdbcClientDetailsService(dataSource);
	}
	*/
    
    @Bean("defaultTokenServices")
    public AuthorizationServerTokenServices defaultTokenServices() {
    	DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(redisTokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(1));
        defaultTokenServices.setTokenEnhancer(customTokenEnhancer());
        return defaultTokenServices;
    }
    
    @Primary
    @Bean(name = "customTokenServices")
    public AuthorizationServerTokenServices customTokenServices() {
    	CustomTokenServices customTokenServices = new CustomTokenServices();
        customTokenServices.setTokenStore(redisTokenStore());
        customTokenServices.setSupportRefreshToken(true);
        customTokenServices.setClientDetailsService(clientDetailsService);
        customTokenServices.setAccessTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(1));
        return customTokenServices;
    }
    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    	security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }
    
    /**
    @Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    	security.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')").checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}
	**/
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	endpoints.authenticationManager(authenticationManager);
    	endpoints.tokenServices(customTokenServices());
    	/**
    	endpoints.tokenStore(redisTokenStore());
    	endpoints.tokenEnhancer(customTokenEnhancer());
    	*/
    }
    
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
//		clients.inMemory().clients(clientDetailsService);
		
		/**
		clients.inMemory()
	        .withClient("app_client")
	        .secret("app_secret")
	        .scopes("read", "write", "trust") 
	        .resourceIds(resourceId)
	        .authorities(AuthorityType.TRUSTED_CLIENT.toString())
	        .authorizedGrantTypes("client_credentials", "password", "authorization_code", "refresh_token")
	        .accessTokenValiditySeconds(accessTokenValiditySeconds)
	        .refreshTokenValiditySeconds(refreshTokenValiditySeconds) 
	       .and()
	        .withClient("web_client")
	        .secret("web_secret")
	        .scopes("read", "write", "trust")
	        .resourceIds(resourceId)
	        .authorities(AuthorityType.TRUSTED_CLIENT.toString())
	        .authorizedGrantTypes("client_credentials", "password", "authorization_code", "refresh_token", "implicit")
	        .accessTokenValiditySeconds(accessTokenValiditySeconds)
	        .refreshTokenValiditySeconds(refreshTokenValiditySeconds);
		*/
		
		/**
		clients.jdbc(dataSource)
			.withClient("client_id")
			.secret("client_secret")
			.scopes("app")
			.authorizedGrantTypes("password", "authorization_code", "refresh_token") // 该client允许的授权类型
			;
		*/
	}

}
