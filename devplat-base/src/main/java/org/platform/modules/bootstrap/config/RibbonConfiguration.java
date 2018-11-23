package org.platform.modules.bootstrap.config;

import org.platform.modules.bootstrap.annotation.ComponentScanExclude;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;

@Configuration
@ComponentScanExclude
public class RibbonConfiguration {
	
	@Bean
	public IRule ribbonRule() {
		return new WeightedResponseTimeRule();
	}
	
	@LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
}