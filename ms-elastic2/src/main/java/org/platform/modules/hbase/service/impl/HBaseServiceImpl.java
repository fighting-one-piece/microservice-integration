package org.platform.modules.hbase.service.impl;

import java.util.Map;

import org.platform.modules.hbase.HBaseUtils;
import org.platform.modules.hbase.service.IHBaseService;
import org.platform.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("hbaseService")
public class HBaseServiceImpl implements IHBaseService {

	@Override
	public Map<String, Object> readDataById(String table, String id) throws BusinessException {
		return HBaseUtils.resultToMap(HBaseUtils.getRecord(table, id, "i"));
	}

	
	
}
