package org.cisiondata.modules.boot;

import javax.annotation.Resource;

import org.cisiondata.modules.bootstrap.BootstrapApplication;
import org.cisiondata.modules.elastic.service.IElasticV2Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class ServiceTest {
	
	@Resource(name = "elasticV2Service")
	private IElasticV2Service elasticV2Service = null;

	@Test
	public void t1() {
		
	}
	
}
