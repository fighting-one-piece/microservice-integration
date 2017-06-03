package org.cisiondata.modules.boot;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.BootstrapApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
//SpringRunner是SpringJunit4ClassRunner新的名称，只是视觉上看起来更简单了
@SpringBootTest(classes = BootstrapApplication.class)
//@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
//该注解可以在一个测试类指定运行Spring Boot为基础的测试
//@SpringApplicationConfiguration(classes = BootstrapApplication.class)
//SpringApplicationConfiguration标记为过时了 1.4.0 前版本  
@WebAppConfiguration 
//使用@WebIntegrationTest注解需要将@WebAppConfiguration注释掉
//@WebIntegrationTest("server.port:0")
//使用0表示端口号随机，也可以具体指定如8888这样的固定端口
public class SpringBootProjectTest {

	@Resource(name = "redisTemplate")
	private RedisTemplate<String, Object> redisTemplate = null;
	
	@Test
	public void testRedisTemplate() {
		redisTemplate.opsForValue().set("name", "zhangsan");
		System.out.println(redisTemplate.opsForValue().get("name"));
	}
	
}
