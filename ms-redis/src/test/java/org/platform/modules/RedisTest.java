package org.platform.modules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.platform.modules.bootstrap.BootstrapApplication;
import org.platform.utils.redis.RedisClusterUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class RedisTest {
	
	@Test
	public void testRedisCluster() {
		RedisClusterUtils.getInstance().set("name", "zhangsan");
		System.out.println(RedisClusterUtils.getInstance().get("name"));
	}
	
}
