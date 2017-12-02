package org.cisiondata.modules;

import org.cisiondata.modules.bootstrap.BootstrapApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class KafkaTest {
	
	@Test
	public void testKafkaProducer() {
	}
	
}
