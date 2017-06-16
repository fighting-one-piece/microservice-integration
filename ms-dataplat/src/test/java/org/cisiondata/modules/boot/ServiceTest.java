package org.cisiondata.modules.boot;

import java.util.List;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.auth.entity.User;
import org.cisiondata.modules.auth.service.IUserService;
import org.cisiondata.modules.bootstrap.BootstrapApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class ServiceTest {
	
	@Resource(name = "userService")
	private IUserService userService = null;
	
	@Test
	public void testUserServiceReadDataByCondition() {
		List<User> users = userService.readDataListByCondition(new Query());
		System.out.println("users:");
		users.forEach(user -> System.out.println(user.getUsername() + " : " + user.getPassword()));
	}
	
}
