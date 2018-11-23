package org.platform.modules.scheduler.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.platform.modules.elastic.utils.ElasticClient;
import org.platform.modules.scheduler.service.IConsumeService;
import org.platform.utils.json.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("elasticConsumeService")
public class ElasticConsumeServiceImpl implements IConsumeService {
	
	private Logger LOG = LoggerFactory.getLogger(ElasticConsumeServiceImpl.class);
	
	@Override
	public void handle(String message) throws RuntimeException {
		if (StringUtils.isBlank(message)) return;
		Map<String, Object> source = GsonUtils.fromJsonToMap(message);
		Client client = ElasticClient.getInstance().getClient();
		IndexRequestBuilder irb = null;
		String index = String.valueOf(source.remove("index"));
		String type = String.valueOf(source.remove("type"));
		if (source.containsKey("_id")) {
			String _id = (String) source.remove("_id");
			irb = client.prepareIndex(index, type, _id).setSource(source);
		} else {
			irb = client.prepareIndex(index, type).setSource(source);
		}
		try {
			irb.execute().get();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	@Override
	public void handle(List<String> messages) throws RuntimeException {
		if (null == messages || messages.size() == 0) return;
		Client client = ElasticClient.getInstance().getClient();
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
		try {
			IndexRequestBuilder irb = null;
			Map<String, Object> osource = null;
			Map<String, Object> nsource = null;
			for (int i = 0, len = messages.size(); i < len; i++) {
				osource = GsonUtils.fromJsonToMap(messages.get(i));
				String index = String.valueOf(osource.remove("index"));
				String type = String.valueOf(osource.remove("type"));
				nsource = removeNotNeedSearchColumn(osource);
				if (nsource.containsKey("_id")) {
					String _id = (String) nsource.remove("_id");
					irb = client.prepareIndex(index, type, _id).setSource(nsource);
				} else {
					irb = client.prepareIndex(index, type).setSource(nsource);
				}
				bulkRequestBuilder.add(irb);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			LOG.error(bulkResponse.buildFailureMessage());
		}
		System.out.println("elastic insert " + messages.size() + " records finish!");
		LOG.info("elastic insert {} records finish!", messages.size());
	}
	
	private Map<String, Object> removeNotNeedSearchColumn(Map<String, Object> map) {
		Map<String, Object> newmap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith("c")) continue;
			newmap.put(key, entry.getValue());
		}
		return newmap;
	}
	
}
