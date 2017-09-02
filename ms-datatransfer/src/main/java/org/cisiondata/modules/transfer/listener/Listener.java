package org.cisiondata.modules.transfer.listener;

import org.cisiondata.utils.exception.BusinessException;

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
