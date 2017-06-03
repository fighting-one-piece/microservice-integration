package org.cisiondata.modules.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("development")
public class ConfigTest {
	
	@Value("${username}")
	private String username = null;
	
	@Test
	public void t1() {
		
	}

}
