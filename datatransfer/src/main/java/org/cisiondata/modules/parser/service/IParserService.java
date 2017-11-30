package org.cisiondata.modules.parser.service;

import java.io.File;

import org.cisiondata.utils.exception.BusinessException;

public interface IParserService {

	/**
	 * 解析处理文件
	 * @param file
	 * @throws BusinessException
	 */
	public void parse(File file) throws BusinessException;
	
}
