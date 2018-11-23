package org.platform.modules.recharge.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.entity.QueryResult;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.recharge.dao.RechargeRecordDAO;
import org.platform.modules.recharge.entity.RechargeRecord;
import org.platform.modules.recharge.entity.RechargeRecordVO;
import org.platform.modules.recharge.entity.TradeStatus;
import org.platform.modules.recharge.service.IRechargeRecordService;
import org.platform.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("rechargeRecordService")
public class RechargeRecordServiceImpl extends GenericServiceImpl<RechargeRecord, Long> implements IRechargeRecordService {

	@Resource(name = "rechargeRecordDAO")
	private RechargeRecordDAO rechargeRecordDAO = null;
	
	@Override
	public GenericDAO<RechargeRecord, Long> obtainDAOInstance() {
		return rechargeRecordDAO;
	}
	
	@Override
	public void update(Object object) throws BusinessException {
		RechargeRecord rechargeRecord = (RechargeRecord) object;
		rechargeRecordDAO.update(rechargeRecord);
	}
	
	@Override
	public RechargeRecord readRechargeRecordByOutTradeNo(String outTradeNo) throws BusinessException {
		if (StringUtils.isBlank(outTradeNo)) throw new BusinessException(ResultCode.PARAM_NULL);
		Query query = new Query();
		query.addCondition("outTradeNo", outTradeNo);
		return rechargeRecordDAO.readDataByCondition(query);
	}
	
	@Override
	public QueryResult<RechargeRecordVO> readRechargeRecordPagination(Integer currentPageNum, Integer rowNumPerPage)
			throws BusinessException {
		if (null == currentPageNum || null == rowNumPerPage) throw new BusinessException(ResultCode.PARAM_NULL);
		Query query = new Query();
		//TODO
		/** query.addCondition("userId", WebUtils.getCurrentUser().getId()); **/
		query.addCondition("tradeStatus", TradeStatus.SUCCESS.name());
		query.setPagination(true);
		query.setCurrentPageNum(currentPageNum);
		query.setRowNumPerPage(rowNumPerPage);
		Map<String, Object> condition = query.getCondition();
		List<RechargeRecord> rechargeRecordList = rechargeRecordDAO.readDataPaginationByCondition(condition);
		List<RechargeRecordVO> rechargeRecordVOList = new ArrayList<RechargeRecordVO>();
		for (int i = 0, len = rechargeRecordList.size(); i < len; i++) {
			RechargeRecord rechargeRecord = rechargeRecordList.get(i);
			RechargeRecordVO rechargeRecordVO = new RechargeRecordVO();
			rechargeRecordVO.setTotalMoney(rechargeRecord.getTotalMoney());
			rechargeRecordVO.setChannel(rechargeRecord.getChannel());
			rechargeRecordVO.setRechargeMode(rechargeRecord.obtainRechargeMode());
			rechargeRecordVO.setInsertTime(rechargeRecord.getInsertTime().getTime()/1000);
			rechargeRecordVOList.add(rechargeRecordVO);
		}
		QueryResult<RechargeRecordVO> qr = new QueryResult<RechargeRecordVO>();
		qr.setResultList(rechargeRecordVOList);
		qr.setTotalRowNum((Long) condition.get(Query.TOTAL_ROW_NUM));
		return qr;
	}
	
}
