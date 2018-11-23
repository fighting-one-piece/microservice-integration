package org.platform.modules.recharge.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.platform.modules.abstr.dao.GenericDAO;
import org.platform.modules.abstr.entity.Query;
import org.platform.modules.abstr.service.impl.GenericServiceImpl;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.recharge.dao.RechargeModeDAO;
import org.platform.modules.recharge.entity.RechargeMode;
import org.platform.modules.recharge.entity.RechargeModeVO;
import org.platform.modules.recharge.service.IRechargeModeService;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.json.GsonUtils;
import org.platform.utils.redis.RedisClusterUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service("rechargeModeService")
public class RechargeModeServiceImpl extends GenericServiceImpl<RechargeMode, Long> 
	implements IRechargeModeService, InitializingBean {

	@Resource(name = "rechargeModeDAO")
	private RechargeModeDAO rechargeModeDAO = null;
	
	private Map<String, RechargeMode> rechargeModeCache = new HashMap<String, RechargeMode>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		initRechargeModeCache();
	}
	
	@Override
	public GenericDAO<RechargeMode, Long> obtainDAOInstance() {
		return rechargeModeDAO;
	}
	
	@Override
	protected void preHandle(Object object) throws BusinessException {
		RechargeMode rechargeMode = (RechargeMode) object;
		Field[] fields = RechargeMode.class.getDeclaredFields();
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field field = fields[i];
				if (!Modifier.isTransient(field.getModifiers())) continue;
				field.setAccessible(true);
				params.put(field.getName(), field.get(rechargeMode));
				field.setAccessible(false);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		if (!params.isEmpty()) rechargeMode.setRule(GsonUtils.fromMapToJson(params));
	}
	
	@Override
	public RechargeMode readRechargeModeByIdentity(String identity) throws BusinessException {
		if (StringUtils.isBlank(identity)) throw new BusinessException(ResultCode.PARAM_NULL);
		RechargeMode rechargeMode = rechargeModeCache.get(identity);
		if (null == rechargeMode) throw new BusinessException(ResultCode.RECHARGE_MODE_NOT_EXIST);
		return rechargeMode;
	}
	
	@Override
	public List<RechargeModeVO> readRechargeModeList() throws BusinessException {
		boolean isExists= RedisClusterUtils.getInstance().exists("mw:recharge:mode");
		if (!isExists) {
			initRechargeModeCache();
			RedisClusterUtils.getInstance().set("mw:recharge:mode", "1");
		}
		List<RechargeModeVO> rechargeModeVOList = new ArrayList<RechargeModeVO>();
		List<RechargeMode> rechargeModeList = new ArrayList<RechargeMode>(rechargeModeCache.values());
		for (int i = 0, len = rechargeModeList.size(); i < len; i++) {
			RechargeMode rechargeMode = rechargeModeList.get(i);
			RechargeModeVO rechargeModeVO = new RechargeModeVO();
			rechargeModeVO.setName(rechargeMode.getName());
			rechargeModeVO.setIdentity(rechargeMode.getIdentity());
			rechargeModeVO.setDesc(rechargeMode.getDesc());
			rechargeModeVO.setTotalMoney(rechargeMode.getTotalMoney());
			rechargeModeVO.setdCoin(rechargeMode.getdCoin());
			rechargeModeVOList.add(rechargeModeVO);
		}
		Collections.sort(rechargeModeVOList, new Comparator<RechargeModeVO>() {
			@Override
			public int compare(RechargeModeVO o1, RechargeModeVO o2) {
				return o1.getdCoin().compareTo(o2.getdCoin());
			}
		});
		return rechargeModeVOList;
	}
	
	private void initRechargeModeCache() {
		Query query = new Query();
		query.addCondition("deleteFlag", false);
		List<RechargeMode> rechargeModeList = rechargeModeDAO.readDataListByCondition(query);
		if (null == rechargeModeList || rechargeModeList.isEmpty()) return;
		for (int i = 0, len = rechargeModeList.size(); i < len; i++) {
			RechargeMode rechargeMode = rechargeModeList.get(i);
			rechargeMode.fillRechargeModeFromRule();
			rechargeModeCache.put(rechargeMode.getIdentity(), rechargeMode);
		}
	}
	
}
