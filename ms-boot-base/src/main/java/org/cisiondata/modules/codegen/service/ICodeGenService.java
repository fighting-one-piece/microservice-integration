package org.cisiondata.modules.codegen.service;

import org.cisiondata.utils.exception.BusinessException;

public interface ICodeGenService {
	
	/**
	 * 生成通用代码
	 * @param clazz
	 * @throws BusinessException
	 */
	public void genGenericCode(Class<?> clazz) throws BusinessException;
	
}
