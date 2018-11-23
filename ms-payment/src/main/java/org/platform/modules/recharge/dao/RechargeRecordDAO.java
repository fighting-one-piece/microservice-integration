package org.platform.modules.recharge.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.recharge.entity.RechargeRecord;
import org.springframework.stereotype.Repository;

@Repository("rechargeRecordDAO")
public interface RechargeRecordDAO extends GenericDAO<RechargeRecord, Long> {
	
}
