package org.platform.modules.recharge.service;

import org.platform.modules.abstr.entity.QueryResult;
import org.platform.modules.abstr.service.IGenericService;
import org.platform.modules.recharge.entity.RechargeRecord;
import org.platform.modules.recharge.entity.RechargeRecordVO;
import org.platform.utils.exception.BusinessException;

public interface IRechargeRecordService extends IGenericService<RechargeRecord, Long> {

	/**
	 * 根据商户订单号读取充值记录
	 * @param outTradeNo
	 * @return
	 * @throws BusinessException
	 */
	public RechargeRecord readRechargeRecordByOutTradeNo(String outTradeNo) throws BusinessException;
	
	/**
	 * 根据用户ID读取分页记录信息
	 * @param userId
	 * @param currentPageNum
	 * @param rowNumPerPage
	 * @return
	 * @throws BusinessException
	 */
	public QueryResult<RechargeRecordVO> readRechargeRecordPagination(Integer currentPageNum, 
			Integer rowNumPerPage) throws BusinessException;
	
	
	
}
