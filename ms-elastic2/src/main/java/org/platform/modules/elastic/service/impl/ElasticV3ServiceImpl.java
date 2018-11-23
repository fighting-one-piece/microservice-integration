package org.platform.modules.elastic.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.platform.modules.abstr.entity.QueryResult;
import org.platform.modules.abstr.web.ResultCode;
import org.platform.modules.elastic.service.IElasticV3Service;
import org.platform.modules.elastic.utils.ElasticClient;
import org.platform.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service("elasticV3Service")
public class ElasticV3ServiceImpl extends ElasticV3AbstractServiceImpl implements IElasticV3Service {
	
	private static final String PHONE_REG = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])(\\d{8}|\\*{4}\\d{4}|\\*{5}\\d{3}|\\*{8})$";
	
	private static final String CALL_REG = "0[0-9]{3}-?[2-9][0-9]{6}|0[0-9]{2}-?[2-9][0-9]{7}|400[0-9]{7}|[2-9][0-9]{6,7}";

	private static final String IDCARD_REG = "\\d{17}(\\d|X|x)|\\d{15}";
	
	private static final String EMAIL_REG = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
	
	private static final String QQ_REG = "[1-9][0-9]{4,9}";
	
	private static final String DIGITAL_REG = "^\\d{1,}$";
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(100);

	@Override
	public QueryResult<Map<String, Object>> readDataList(String keywords) throws BusinessException {
		if (StringUtils.isBlank(keywords)) throw new RuntimeException("keywords is null");
		QueryResult<Map<String, Object>> qr = new QueryResult<Map<String, Object>>();
		String[] types = defaultTypes();
		for (int i = 0, len = types.length; i < len; i++) {
			QueryResult<Map<String, Object>> subqr = readDataList(types[i], keywords); 
			qr.getResultList().addAll(subqr.getResultList());
			qr.setTotalRowNum(qr.getTotalRowNum() + subqr.getTotalRowNum());
		}
		return qr;
	}
	
	@Override
	public List<Map<String, Object>> readDataHitList(String keywords) throws BusinessException {
		List<DataHitTask> tasks = new ArrayList<DataHitTask>();
		for (String type : types) {
			tasks.add(new DataHitTask(type, keywords));
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Future<Map<String, Object>>> fs = null;
		try {
			fs = executorService.invokeAll(tasks, 20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
		for (int i = 0, len = fs.size(); i < len; i++) {
			Map<String, Object> result = null;
			try {
				result = fs.get(i).get();
			} catch (Exception e) {
				DataHitTask task = tasks.get(i);
				result = new HashMap<String, Object>();
				result.put("index", task.getIndex());
				result.put("type", task.getType());
				result.put("hits", -1);
				LOG.error(e.getMessage(), e);
			}
			if (!result.isEmpty())
				resultList.add(result);
		}
		if (resultList.size() == 0)
			throw new BusinessException(ResultCode.NOT_FOUNT_DATA);
		return resultList;
	}
	
	private QueryResult<Map<String, Object>> readDataList(String type, String keywords) {
		QueryResult<Map<String, Object>> qr = new QueryResult<Map<String, Object>>();
		try {
			SearchRequestBuilder searchRequestBuilder = ElasticClient.getInstance().getClient()
					.prepareSearch(type_index_mapping.get(type)).setTypes(type);
			searchRequestBuilder.setQuery(buildBoolQuery(type, keywords));
			searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
			searchRequestBuilder.setSize(1000);
			searchRequestBuilder.setExplain(false);
			SearchResponse response = searchRequestBuilder.execute().actionGet();
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			SearchHit[] hits = response.getHits().getHits();
			for (int i = 0, len = hits.length; i < len; i++) {
				resultList.add(hits[i].getSource());
			}
			qr.setResultList(resultList);
			qr.setTotalRowNum(response.getHits().getTotalHits());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return qr;
	}

	private BoolQueryBuilder buildBoolQuery(String type, String keywords) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		Set<String> attributes = type_attributes_mapping.get(type);
		if (null == attributes || attributes.size() == 0) return boolQueryBuilder;
		String[] keywordArray = keywords.trim().indexOf(" ") == -1 ? new String[]{keywords} : keywords.split(" ");
		for (int i = 0, len = keywordArray.length; i < len; i++) {
			String keyword = keywordArray[i];
			BoolQueryBuilder subBoolQueryBuilder = new BoolQueryBuilder();
			for (String attribute : attributes) {
				subBoolQueryBuilder.should(isIdentity(keyword) ? QueryBuilders.termQuery(attribute, keyword) 
					: QueryBuilders.matchPhraseQuery(attribute, keyword));
			}
			boolQueryBuilder.must(subBoolQueryBuilder);
		}
		return boolQueryBuilder;
	}
	
	private boolean isIdentity(String keyword) {
		return isMatchRegex(keyword, PHONE_REG) || isMatchRegex(keyword, IDCARD_REG) || isMatchRegex(keyword, CALL_REG) 
				|| isMatchRegex(keyword, EMAIL_REG) || isMatchRegex(keyword, QQ_REG) || isMatchRegex(keyword, DIGITAL_REG)
					? true : false;
	}
	
	private boolean isMatchRegex(String keyword, String regex) {
		return Pattern.compile(regex).matcher(keyword).find();
	}
	
	class DataHitTask implements Callable<Map<String, Object>> {

		private String index = null;

		private String type = null;

		private String keywords = null;

		public DataHitTask(String type, String keywords) {
			this.index = type_index_mapping.get(type);
			this.type = type;
			this.keywords = keywords;
		}
		
		public DataHitTask(String index, String type, String keywords) {
			this.index = index;
			this.type = type;
			this.keywords = keywords;
		}

		@Override
		public Map<String, Object> call() throws Exception {
			Map<String, Object> result = new HashMap<String, Object>();
			QueryResult<Map<String, Object>> qr = readDataList(type, keywords);
			if (qr.getTotalRowNum() == 0) return result;
			result.put("index", index);
			result.put("type", type);
			result.put("hits", qr.getTotalRowNum());
			return result;
		}

		public String getIndex() {
			return index;
		}

		public String getType() {
			return type;
		}
		
	}
	
}
