package org.cisiondata.modules.auth;

import java.util.Date;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.BaseBootstrapApplication;
import org.cisiondata.modules.oauth.dao.UserDAO;
import org.cisiondata.modules.oauth.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

//@EnableHystrix
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.cisiondata.modules"})
//@EnableCircuitBreaker
@EnableDiscoveryClient
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BaseBootstrapApplication.class)
@WebAppConfiguration 
public class DAOTest {
	
	@Resource(name = "userDAO")
	private UserDAO userDAO = null;
	
	@Test
	public void testInsertUser() {
		User user = new User();
		user.setUsername("admin1");
		user.setPassword(new BCryptPasswordEncoder().encode("admin1"));
		user.setSalt("admin1");
		user.setCreateTime(new Date());
		user.setDeleteFlag(false);
		userDAO.insert(user);
	}
	
	@Test
	public void testReadUserById() {
		System.err.println(userDAO.readDataByPK(1L));
	}

}
