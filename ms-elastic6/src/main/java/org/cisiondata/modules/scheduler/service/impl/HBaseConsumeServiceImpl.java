package org.cisiondata.modules.scheduler.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.cisiondata.modules.hbase.HBaseUtils;
import org.cisiondata.modules.scheduler.service.IConsumeService;
import org.cisiondata.utils.json.GsonUtils;
import org.cisiondata.utils.serde.SerializerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("hbaseConsumeService")
public class HBaseConsumeServiceImpl implements IConsumeService {
	
	private Logger LOG = LoggerFactory.getLogger(HBaseConsumeServiceImpl.class);
	
	@Override
	public void handle(String message) throws RuntimeException {
		if (StringUtils.isBlank(message)) return;
		Map<String, Object> source = GsonUtils.fromJsonToMap(message);
		source.remove("index");
		String tableName = String.valueOf(source.remove("type"));
		String rowKey = String.valueOf(source.remove("_id"));
		Put put = new Put(Bytes.toBytes(rowKey));
		for (Map.Entry<String, Object> entry : source.entrySet()) {
			String column = entry.getKey();
			String family = column.startsWith("c") ? "i" : "s";
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), SerializerUtils.write(entry.getValue()));
		}
		try {
			HBaseUtils.insertRecord(tableName, put);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handle(List<String> messages) throws RuntimeException {
		if (null == messages || messages.size() == 0) return;
		try {
			Map<String, List<Put>> map = new HashMap<String, List<Put>>();
			Map<String, Object> source = null;
			for (int i = 0, len = messages.size(); i < len; i++) {
				source = GsonUtils.fromJsonToMap(messages.get(i));
				source.remove("index");
				String tableName = String.valueOf(source.remove("type"));
				List<Put> puts = map.get(tableName);
				if (null == puts) {
					puts = new ArrayList<Put>();
					map.put(tableName, puts);
				}
				String rowKey = String.valueOf(source.remove("_id"));
				Put put = new Put(Bytes.toBytes(rowKey));
				for (Map.Entry<String, Object> entry : source.entrySet()) {
					String column = entry.getKey();
					String family = column.startsWith("c") ? "i" : "s";
					put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), SerializerUtils.write(entry.getValue()));
				}
				puts.add(put);
			}
			for (Map.Entry<String, List<Put>> entry : map.entrySet()) {
				try {
					HBaseUtils.insertRecords(entry.getKey(), entry.getValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("hbase insert " + messages.size() + " records finish!");
			LOG.info("hbase insert {} records finish!", messages.size());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
}
