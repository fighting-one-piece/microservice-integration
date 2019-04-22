package org.platform.modules;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.platform.modules.bootstrap.BootstrapApplication;
import org.platform.modules.quartz.service.IQuartzService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration
public class ServiceTest {
	
	@Resource(name = "quartzService")
	private IQuartzService quartzService = null;
	
	@Test
	public void testInsertJob() {
		quartzService.insert("job-group-1", "default-job-1", "org.platform.modules.quartz.task.DefaultJob", 
			"trigger-group", "trigger-1", "0 0/1 * * * ?");
		Map<String, Object> jobData = new HashMap<String, Object>();
		jobData.put("injectValue1", 100);
		jobData.put("injectValue2", "quartz");
		quartzService.insert("job-group-1", "default-job-2", "org.platform.modules.quartz.task.DefaultJob", 
			jobData, "trigger-group", "trigger-2", "0 0/1 * * * ?");
	}
	
}
