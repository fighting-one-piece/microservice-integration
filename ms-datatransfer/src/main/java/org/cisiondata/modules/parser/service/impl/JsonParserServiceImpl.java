package org.cisiondata.modules.parser.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.file.DefaultLineHandler;
import org.cisiondata.utils.file.FileUtils;
import org.cisiondata.utils.json.GsonUtils;
import org.springframework.stereotype.Service;

@Service("jsonParserService")
public class JsonParserServiceImpl extends AbstractParserServiceImpl {
	
	@Override
	public void parse(File file) throws BusinessException {
		try {
			List<String> lines = FileUtils.read(new FileInputStream(file), new DefaultLineHandler());
			if (lines.size() > 1) throw new BusinessException("file error!");
			Map<String, Object> params = GsonUtils.fromJsonToMap(lines.get(0));
			LOG.info("params: {}", params);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	
}
