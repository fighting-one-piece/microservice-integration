package org.platform.modules.recharge.service;

import java.util.List;

import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.recharge.entity.RechargeMode;
import org.platform.modules.recharge.entity.RechargeModeVO;
import org.platform.utils.exception.BusinessException;

public interface IRechargeModeService extends IGenericService<RechargeMode, Long> {
	
	/**
	 * 根据identity标识读取充值方式
	 * @param identity
	 * @return
	 * @throws BusinessException
	 */
	public RechargeMode readRechargeModeByIdentity(String identity) throws BusinessException;
	
	/**
	 * 读取充值方式列表
	 * @return
	 * @throws BusinessException
	 */
	public List<RechargeModeVO> readRechargeModeList() throws BusinessException;

}
