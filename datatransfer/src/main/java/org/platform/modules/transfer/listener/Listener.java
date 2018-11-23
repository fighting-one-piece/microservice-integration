package org.platform.modules.transfer.listener;

import org.platform.utils.exception.BusinessException;

public interface Listener {

	/**
	 * 启动监听
	 * @return
	 * @throws BusinessException
	 */
	public boolean startup() throws BusinessException;
	
	/**
	 * 关闭监听
	 * @return
	 * @throws BusinessException
	 */
	public boolean shutdown() throws BusinessException;
	
}
