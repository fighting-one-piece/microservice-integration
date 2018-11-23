package org.platform.modules.codegen.service;

import org.platform.utils.exception.BusinessException;

public interface ICodeGenService {
	
	/**
	 * 生成通用代码
	 * @param clazz
	 * @throws BusinessException
	 */
	public void genGenericCode(Class<?> clazz) throws BusinessException;
	
}
