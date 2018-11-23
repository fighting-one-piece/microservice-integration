package org.platform.modules.parser.service.impl;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.platform.modules.parser.service.IParserService;
import org.platform.modules.transfer.service.IConfigService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.file.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service("abstractParserService")
public class AbstractParserServiceImpl implements IParserService {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	public static String PREFIX = "https://api.platform.cn/devplat/ext/api/v1";

	protected String directory = null;
	
	@Resource(name = "configService")
	protected IConfigService configService = null;
	
	@Resource(name = "templateEngine")
	protected SpringTemplateEngine templateEngine = null;
	
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
	}
	
	
	
}
