package org.cisiondata.modules.boot;

import org.cisiondata.modules.bootstrap.BaseBootstrapApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaseBootstrapApplication.class)
@WebAppConfiguration 
@EnableHystrix
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.cisiondata.modules"})
@EnableCircuitBreaker
@EnableDiscoveryClient
public class DAOTest {
	
}
