package org.cisiondata.modules.hbase.service.impl;

import java.util.Map;

import org.cisiondata.modules.hbase.HBaseUtils;
import org.cisiondata.modules.hbase.service.IHBaseService;
import org.cisiondata.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("hbaseService")
public class HBaseServiceImpl implements IHBaseService {

	@Override
	public Map<String, Object> readDataById(String table, String id) throws BusinessException {
		return HBaseUtils.resultToMap(HBaseUtils.getRecord(table, id, "i"));
	}

	
	
}
