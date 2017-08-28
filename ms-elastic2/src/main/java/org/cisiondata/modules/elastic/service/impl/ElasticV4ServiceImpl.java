package org.cisiondata.modules.elastic.service.impl;

import java.util.Map;

import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.modules.elastic.service.IElasticV4Service;
import org.cisiondata.modules.elastic.utils.ElasticClient;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.param.ParamsUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("elasticV4Service")
public class ElasticV4ServiceImpl implements IElasticV4Service {
	
	private Logger LOG = LoggerFactory.getLogger(ElasticV4ServiceImpl.class);

	@Override
	public Object readDataList(String index, String type, String keyword, int deleteFlag) throws BusinessException {
		ParamsUtils.checkNotNull(index, "index is null");
		ParamsUtils.checkNotNull(type, "type is null");
		ParamsUtils.checkNotNull(keyword, "keyword is null");
		Client client = ElasticClient.getInstance().getClient();
		QueryBuilder queryBuilder = QueryBuilders.termQuery("c136", keyword);
		SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(queryBuilder)
				.setSearchType(SearchType.QUERY_AND_FETCH).setScroll(new TimeValue(60000))
					.setSize(1000).setExplain(false).execute().actionGet();
		if (deleteFlag == 0) {
			QueryResult<Map<String, Object>> qr = new QueryResult<>();
			qr.setTotalRowNum(response.getHits().getTotalHits());
			SearchHit[] hitArray = response.getHits().getHits();
			for (int i = 0, len = Math.min(hitArray.length, 5); i < len; i++) {
				qr.getResultList().add(hitArray[i].getSource());
			}
			return qr;
		} else if (deleteFlag == 1) {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			while (true) {
				SearchHit[] hitArray = response.getHits().getHits();
				for (int i = 0, len = hitArray.length; i < len; i++) {
					DeleteRequestBuilder request = client.prepareDelete(index, type, hitArray[i].getId());
					bulkRequest.add(request);
				}
				BulkResponse bulkResponse = bulkRequest.execute().actionGet();
				if (bulkResponse.hasFailures()) {
					LOG.error(bulkResponse.buildFailureMessage());
				}
				if (hitArray.length == 0) break;
				response = client.prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(60000)).execute().actionGet();
			}
			return true;
		}
		return false;
	}

}
