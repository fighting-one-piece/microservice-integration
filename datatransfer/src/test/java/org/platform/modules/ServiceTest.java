package org.platform.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.platform.modules.bootstrap.BootstrapApplication;
import org.platform.utils.file.FileUtils;
import org.platform.utils.param.ParamsUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootstrapApplication.class)
@WebAppConfiguration 
public class ServiceTest {
	
	@Resource(name = "templateEngine")
	private SpringTemplateEngine templateEngine = null;
	
	@Test
	public void t1() {
		Context context = new Context();
		context.setVariable("query", "13512345678");
		context.setVariable("totalRowNum", 100L);
		String result = templateEngine.process("xml/result", context);
		System.err.println(result);
	}
	
	@Test
	public void t2() {
		Context context = new Context();
		context.setVariable("name", "zhangsanjiushiwo");
		context.setVariable("subscriptionDate", new Date());
		context.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
		String result = templateEngine.process("text/result", context);
		System.err.println(result);
	}
	
	@Test
	public void t3() {
		Context context = new Context();
		context.setVariable("name", "zhangsanjiushiwo");
		context.setVariable("subscriptionDate", new Date());
		context.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
		String result = templateEngine.process("html/result", context);
		System.err.println(result);
	}
	
	@Test
	public void t4() {
		Context context = new Context();
		context.setVariable("lx", "logistics");
		context.setVariable("accessId", "9A27E1f5F3AEeE71");
		context.setVariable("query", "13512345678");
		context.setVariable("scrollId", 1);
		context.setVariable("rowNumPerPage", 10);
		context.setVariable("token", "abcdefghijklmnopqrst");
		context.setVariable("totalRowNum", 100L);
		context.setVariable("nextScrollId", 2);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", "zhangsan");
		map1.put("age", 20);
		resultList.add(map1);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("name", "lisi");
		map2.put("age", 22);
		resultList.add(map2);
		context.setVariable("resultList", resultList);
		String result = templateEngine.process("text/response", context);
		System.err.println(result);
		FileUtils.write("F:\\a.xml", result);
	}
	
	@Test
	public void t5() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("query", "13512345678");
		params.put("scrollId", "1");
		params.put("rowNumPerPage", "10");
		System.err.println(ParamsUtils.genToken(params));
	}
	
}
