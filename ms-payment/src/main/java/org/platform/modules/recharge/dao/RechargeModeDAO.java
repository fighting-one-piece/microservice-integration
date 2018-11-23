package org.platform.modules.recharge.dao;

import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.recharge.entity.RechargeMode;
import org.springframework.stereotype.Repository;

@Repository("rechargeModeDAO")
public interface RechargeModeDAO extends GenericDAO<RechargeMode, Long> {

}
