package org.cisiondata.modules.parser.service.impl;

import java.io.File;

import javax.annotation.PostConstruct;

import org.cisiondata.modules.parser.service.IParserService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.file.FileUtils;
import org.cisiondata.utils.file.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("parserService")
public class ParserServiceImpl implements IParserService {
	
	private Logger LOG = LoggerFactory.getLogger(ParserServiceImpl.class);

	private String directory = null;
	
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
		FileUtils.write(directory + File.separator + file.getName(), "hello");
	}
	
	
}
