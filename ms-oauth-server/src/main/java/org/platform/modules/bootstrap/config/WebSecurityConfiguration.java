package org.platform.modules.bootstrap.config;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity(debug = false)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Resource(name = "userDetailsExtService")
	private UserDetailsService userDetailsService = null;
	
	public UserDetailsService userDetailsService() {
		return userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.anonymous().disable(); 
		http.formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/", true)
            .and().logout().logoutUrl("/logout").permitAll();
        http.sessionManagement().maximumSessions(1).expiredUrl("/expired");
        http.exceptionHandling().accessDeniedPage("/accessDenied");
        //登录后记住用户，下次自动登录,数据库中必须存在名为persistent_logins的表  
        http.rememberMe().tokenValiditySeconds(1209600);  
		http.httpBasic();
        //http.authorizeRequests().antMatchers("/oauth/authorize").permitAll();
        //http.authorizeRequests().anyRequest().authenticated();
        http.authorizeRequests().anyRequest().fullyAuthenticated();
	}

	@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**", "/**/favicon.ico");
    }

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
		auth.eraseCredentials(false);   
//		auth.inMemoryAuthentication().withUser("").password("").roles("");
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
