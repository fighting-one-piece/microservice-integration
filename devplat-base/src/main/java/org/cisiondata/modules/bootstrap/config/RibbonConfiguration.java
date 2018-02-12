package org.cisiondata.modules.bootstrap.config;

import org.cisiondata.modules.bootstrap.annotation.ComponentScanExclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.WeightedResponseTimeRule;

@Configuration
@ComponentScanExclude
public class RibbonConfiguration {
	
	@Bean
	public IRule ribbonRule() {
		return new WeightedResponseTimeRule();
	}
	
	
}