package org.cisiondata.modules.parser.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.cisiondata.modules.parser.service.IParserService;
import org.cisiondata.modules.transfer.service.IConfigService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.file.FileUtils;
import org.cisiondata.utils.file.PropertiesUtils;
import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.json.GsonUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service("parserService")
public class ParserServiceImpl implements IParserService {
	
	private Logger LOG = LoggerFactory.getLogger(ParserServiceImpl.class);
	
	private static String PREFIX = "https://api.cisiondata.cn/devplat/ext/api/v1";

	private String directory = null;
	
	@Resource(name = "configService")
	private IConfigService configService = null;
	
	@Resource(name = "templateEngine")
	private SpringTemplateEngine templateEngine = null;
	
	@PostConstruct
	public void postConstruct() {
		String userDir = System.getProperty("user.dir");
		String configFile = userDir + File.separator + "config" + File.separator + "sys-conf.properties";
		this.directory = PropertiesUtils.getProperty(configFile, "transfer.file.o.path");
		File dir = new File(directory);
		if (!dir.exists()) dir.mkdirs();
		LOG.info("parser output path: {}", directory);
	}
	
	@Override
	public void parse(File file) throws BusinessException {
		Map<String, String> params = extractParams(file);
		Context context = new Context();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			context.setVariable(entry.getKey(), entry.getValue());
		}
		String lx = params.remove("lx");
		String url = configService.readSystemConfigValue("req.url." + lx);
		String jsonResult = HttpUtils.sendGet(PREFIX + url, params);
		Map<String, Object> map = GsonUtils.fromJsonToMap(jsonResult);
		String template = configService.readSystemConfigValue("res.tpl.r." + lx);
		String responseCode = (String) map.get("code");
		if ("1".equalsIgnoreCase(responseCode)) {
			Map<String, Object> data = GsonUtils.fromJsonToMap(map.get("data").toString());
			if (null != data && !data.isEmpty() && data.containsKey("resultList")) {
				List<Map<String, Object>> resultList = GsonUtils.fromJsonToList(data.get("resultList").toString());
				context.setVariable("resultList", resultList);
			}
		} else {
			template = configService.readSystemConfigValue("res.tpl.e." + lx);
			context.setVariable("code", responseCode);
			context.setVariable("failure", map.get("failure"));
		}
		String fileName = "CD-Q-" + lx.toUpperCase() + "-" + params.get("query") + ".xml";
		String path = directory + File.separator + fileName;
		String content = templateEngine.process(template, context);
		System.err.println(content);
		FileUtils.write(path, content);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> extractParams(File file) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			List<Attribute> attributes = document.getRootElement().attributes();
			for (int i = 0, len = attributes.size(); i < len; i++) {
				Attribute attribute = attributes.get(i);
				String name = attribute.getName();
				if ("ACCESSID".equals(name)) {
					name = "accessId";
				} else if ("LX".equals(name)) {
					name = "lx";
				} else if ("QUERY".equals(name)) {
					name = "query";
				} else if ("ROWNUMPERPAGE".equals(name)) {
					name = "rowNumPerPage";
				} else if ("SCROLLID".equals(name)) {
					name = "scrollId";
				} else if ("TOKEN".equals(name)) {
					name = "token";
				}
				params.put(name, attribute.getValue());
			}
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
		}
		return params;
	}
	
	
	
}
