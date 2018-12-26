package org.platform.modules.boot;

import org.junit.runner.RunWith;
import org.platform.modules.bootstrap.BaseBootstrapApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaseBootstrapApplication.class)
@WebAppConfiguration 
@EnableHystrix
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.platform.modules"})
@EnableCircuitBreaker
@EnableDiscoveryClient
public class DAOTest {
	
}
