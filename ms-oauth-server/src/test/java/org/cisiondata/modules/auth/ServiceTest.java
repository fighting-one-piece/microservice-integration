package org.cisiondata.modules.auth;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.BootstrapApplication;
import org.cisiondata.modules.oauth.entity.Client;
import org.cisiondata.modules.oauth.entity.User;
import org.cisiondata.modules.oauth.service.IClientService;
import org.cisiondata.modules.oauth.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Rollback
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class ServiceTest {
	
	@Resource(name = "userService")
	private IUserService userService = null;
	
	@Resource(name = "clientService")
	private IClientService clientService = null;
	
	@Test
	public void testInsertClient() {
		Client client = new Client();
		client.setClientId("web_client");
		client.setClientSecret("web_secret");
		client.setResourceIds("oauth2_resource_id");
		client.setScope("read,write,trust");
		client.setAuthorities("TRUSTED");
		client.setAuthorizedGrantTypes("client_credentials,password,authorization_code,refresh_token");
		client.setAccessTokenValidity(10000);
		client.setRefreshTokenValidity(30000);
		Calendar calendar = Calendar.getInstance();
		client.setCreateTime(calendar.getTime());
		calendar.add(Calendar.DATE, 30);
		client.setExpireTime(calendar.getTime());
		client.setDeleteFlag(false);
		clientService.insert(client);
	}
	
	@Test
	public void testReadClientByClientId() {
		Client client = clientService.readClientByClientId("web_client");
		System.err.println(client);
	}

	@Test
	public void testInsertUser() {
		User user = new User();
		user.setUsername("test");
		user.setPassword(new BCryptPasswordEncoder().encode("test"));
		user.setSalt("test");
		user.setCreateTime(new Date());
		user.setDeleteFlag(false);
		userService.insert(user);
	}
	
}
