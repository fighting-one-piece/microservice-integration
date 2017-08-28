package org.cisiondata.modules;

import java.util.Map;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.entity.QueryResult;
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

	@SuppressWarnings("unchecked")
	@Test
	public void t1() {
		QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) 
				elasticV2Service.readDataList("qq", "qqqunrelation", "nick", "明月", 1, 1, 2);
		System.err.println(qr.getTotalRowNum());
		for (Map<String, Object> result : qr.getResultList()) {
			System.err.println(result);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void t2() {
		QueryResult<Map<String, Object>> qr = (QueryResult<Map<String, Object>>) 
				elasticV2Service.readDataList("car-v1", "car", null, "13336036599", 1, 1, 2);
		System.err.println(qr.getTotalRowNum());
		for (Map<String, Object> result : qr.getResultList()) {
			System.err.println(result);
		}
	}
	
}
