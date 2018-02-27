package org.cisiondata.modules.auth;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.entity.Query;
import org.cisiondata.modules.bootstrap.BaseBootstrapApplication;
import org.cisiondata.modules.oauth.dao.RoleDAO;
import org.cisiondata.modules.oauth.dao.UserDAO;
import org.cisiondata.modules.oauth.dao.UserRoleDAO;
import org.cisiondata.modules.oauth.entity.Role;
import org.cisiondata.modules.oauth.entity.User;
import org.cisiondata.modules.oauth.entity.UserRole;
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
	
	@Resource(name = "roleDAO")
	private RoleDAO roleDAO = null;
	
	@Resource(name = "userRoleDAO")
	private UserRoleDAO userRoleDAO = null;
	
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
	public void testInitData() {
		Role role1 = new Role();
		role1.setName("用户管理员");
		role1.setIdentity(Role.USER_ADMIN);
		role1.setDesc("负责用户的管理");
		role1.setDeleteFlag(false);
		roleDAO.insert(role1);
		System.err.println("role1: " + role1.getId());
		Role role2 = new Role();
		role2.setName("客户管理员");
		role2.setIdentity(Role.CLIENT_ADMIN);
		role2.setDesc("负责客户的管理");
		role2.setDeleteFlag(false);
		roleDAO.insert(role2);
		System.err.println("role2: " + role2.getId());
		
		User user1 = new User();
		user1.setUsername("uadmin");
		user1.setPassword(new BCryptPasswordEncoder().encode("uadmin"));
		user1.setSalt("uadmin");
		user1.setDeleteFlag(false);
		
		User user2 = new User();
		user2.setUsername("cadmin");
		user2.setPassword(new BCryptPasswordEncoder().encode("cadmin"));
		user2.setSalt("cadmin");
		user2.setDeleteFlag(false);
		
		Calendar calendar = Calendar.getInstance();
		user1.setCreateTime(calendar.getTime());
		user2.setCreateTime(calendar.getTime());
		calendar.add(Calendar.YEAR, 10);
		user1.setExpireTime(calendar.getTime());
		user2.setExpireTime(calendar.getTime());
		
		userDAO.insert(user1);
		System.err.println("user1: " + user1.getId());
		
		userDAO.insert(user2);
		System.err.println("user2: " + user2.getId());
		
		UserRole ur1 = new UserRole();
		ur1.setUserId(user1.getId());
		ur1.setRoleId(role1.getId());
		ur1.setPriority(1);
		userRoleDAO.insert(ur1);
		System.err.println("ur1: " + ur1.getId());
		
		UserRole ur2 = new UserRole();
		ur2.setUserId(user2.getId());
		ur2.setRoleId(role2.getId());
		ur2.setPriority(1);
		userRoleDAO.insert(ur2);
		System.err.println("ur2: " + ur2.getId());
		
	}
	
	@Test
	public void testReadUserById() {
		System.err.println(userDAO.readDataByPK(1L));
	}
	
	@Test
	public void testReadUserRoleListByUserId() {
		Query query = new Query();
		query.addCondition("userId", 1L);
		List<UserRole> urs = userRoleDAO.readDataListByCondition(query);
		urs.forEach(ur -> System.err.println(ur));
	}

}
