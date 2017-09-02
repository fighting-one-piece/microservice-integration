package org.cisiondata.modules.transfer.service.impl;

import java.io.File;
import java.util.Properties;

import org.cisiondata.modules.transfer.service.IConfigService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.file.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("configService")
public class ConfigServiceImpl implements IConfigService, InitializingBean {

	private Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);
	
	private Properties properties = null;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		loadSystemConfig();
	}
	
	@Override
	public String readSystemConfigValue(String key) throws BusinessException {
		if (null == properties) loadSystemConfig();
		return properties.getProperty(key);
	}
	
	@Override
	public void refreshSystemConfig() throws BusinessException {
		LOG.info("refresh system config!");
		loadSystemConfig();
	}
	
	private void loadSystemConfig() {
		String userDir = System.getProperty("user.dir");
		String path = userDir + File.separator + "config" + File.separator + "sys-conf.properties";
		LOG.info("config file path: {}", path);
		File sysConfigFile = new File(path);
		if (!sysConfigFile.exists()) {
			throw new BusinessException("system config file not exist!");
		}
		this.properties = PropertiesUtils.newInstance(path);
	}

}
