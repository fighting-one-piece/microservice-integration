package org.platform.modules.boot;

import java.util.Calendar;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.platform.modules.bootstrap.BootstrapApplication;
import org.platform.modules.codegen.service.ICodeGenService;
import org.platform.modules.user.entity.RoleResource;
import org.platform.modules.user.entity.User;
import org.platform.modules.user.service.IUserBizService;
import org.platform.modules.user.service.IUserService;
import org.platform.utils.endecrypt.EndecryptUtils;
import org.platform.utils.endecrypt.IDUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration
public class ServiceTest {
	
	@Resource(name = "userService")
	protected IUserService userService = null;
	
	@Resource(name = "userBizService")
	protected IUserBizService userBizService = null;
	
	@Resource(name = "codeGenService")
	protected ICodeGenService codeGenService = null;
	
	@Test
	public void testGenGenericCode() {
		codeGenService.genGenericCode(RoleResource.class);
//		codeGenService.genGenericCode(org.platform.modules.user.entity.Resource.class);
	}
	
	@Test
	public void testInsertUser() {
		User user = new User();
		user.setAccount("admin");
		String password = "admin123";
		String salt = IDUtils.genUUID();
		user.setPassword(EndecryptUtils.encryptPassword(password, salt));
		user.setSalt(salt);
		user.setNickName("admin");
		user.setRealName("管理员");
		user.setIdCard("653101199001011001");;
		user.setMobilePhone("13512345678");
		user.setEmail("123456@qq.com");
		Calendar calendar = Calendar.getInstance();
		user.setCreateTime(calendar.getTime());
		calendar.add(Calendar.YEAR, 1);
		user.setExpireTime(calendar.getTime());
		user.setDeleteFlag(false);
		userService.insert(user);
	}
	
}
