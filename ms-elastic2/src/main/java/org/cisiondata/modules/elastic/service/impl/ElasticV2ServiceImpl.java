package org.cisiondata.modules.elastic.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.modules.elastic.entity.SearchParams;
import org.cisiondata.modules.elastic.service.IElasticV2Service;
import org.cisiondata.modules.elastic.utils.ElasticClient;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.idgen.MD5Utils;
import org.cisiondata.utils.param.ParamsUtils;
import org.cisiondata.utils.redis.RedisClusterUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("elasticV2Service")
public class ElasticV2ServiceImpl extends ElasticV2AbstractServiceImpl implements IElasticV2Service {
	
	private Logger LOG = LoggerFactory.getLogger(ElasticV2ServiceImpl.class);
	
	@SuppressWarnings("unused")
	private static final String CN_REG = "[\\u4e00-\\u9fa5]+";
	
	private static final String PHONE_REG = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])(\\d{8}|\\*{4}\\d{4}|\\*{5}\\d{3}|\\*{8})$";
	
	private static final String CALL_REG = "0[0-9]{3}-?[2-9][0-9]{6}|0[0-9]{2}-?[2-9][0-9]{7}|400[0-9]{7}|[2-9][0-9]{6,7}";

	private static final String IDCARD_REG = "\\d{17}(\\d|X|x)|\\d{15}";
	
	private static final String EMAIL_REG = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
	
	private static final String QQ_REG = "[1-9][0-9]{4,9}";
	
	private static final String DIGITAL_REG = "^\\d{1,}$";
	
	@Override
	public Object readDataList(String indices, String types, String fields, String keywords, 
			int highLight, Integer currentPageNum, Integer rowNumPerPage) throws BusinessException {
		ParamsUtils.checkNotNull(indices, "indices is null");
		ParamsUtils.checkNotNull(types, "types is null");
		ParamsUtils.checkNotNull(keywords, "keywords is null");
		SearchParams params = new SearchParams(indices, types, fields, keywords, highLight, currentPageNum, rowNumPerPage);
		QueryResult<Map<String, Object>> qr = null;
		try {
			qr = readDataList(params);
			if (!params.isPagination()) return qr.getResultList();
			qr.setResultList(qr.getPaginationResultList(currentPageNum, rowNumPerPage));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return qr;
	}
	
	@Override
	public QueryResult<Map<String, Object>> readDataList(String indices, String types, String fields, 
			String keywords, int highLight, String scrollId) throws BusinessException {
		if (StringUtils.isBlank(indices)) throw new RuntimeException("indices is null");
		if (StringUtils.isBlank(types)) throw new RuntimeException("types is null");
		if (StringUtils.isBlank(keywords)) throw new RuntimeException("keywords is null");
		try {
			SearchParams params = new SearchParams(indices, types, fields, keywords, highLight, scrollId);
			return readDataList(params);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private QueryResult<Map<String, Object>> readDataList(SearchParams params) {
		String cacheKey = genCacheKey(params);
		Object cacheObject = RedisClusterUtils.getInstance().get(cacheKey);
		if (null != cacheObject) {
			return (QueryResult<Map<String, Object>>) cacheObject;
		} 
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Set<String>> attributes = extractAttributes(params);
		SearchRequestBuilder searchRequestBuilder = ElasticClient.getInstance().getClient()
				.prepareSearch(params.indices()).setTypes(params.types());
		searchRequestBuilder.setExplain(false);
		searchRequestBuilder.setQuery(buildBoolQuery(params.keywords(), attributes));
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setScroll(TimeValue.timeValueMinutes(3));
		searchRequestBuilder.setSize(200);
		if (params.isHighLight()) buildHighLight(searchRequestBuilder, attributes);
		SearchResponse response = searchRequestBuilder.execute().actionGet();
		LOG.info("scrollId: " + response.getScrollId());
		if (params.isScroll()) {
			response = ElasticClient.getInstance().getClient().prepareSearchScroll(
				params.getScrollId()).setScroll(TimeValue.timeValueMinutes(3)).execute().actionGet();
		}
		SearchHit[] hits = response.getHits().getHits();
		Map<String, Object> source = null;
		for (int i = 0, len = hits.length; i < len; i++) {
			SearchHit hit = hits[i];
			source = hit.getSource();
			if (params.isHighLight()) {
				wrapperHighLight(source, hit.getHighlightFields());
			}
			resultList.add(source);
		}
		QueryResult<Map<String, Object>> qr = new QueryResult<>();
		qr.setResultList(resultList);
		qr.setTotalRowNum(response.getHits().getTotalHits());
		RedisClusterUtils.getInstance().set(cacheKey, qr, 600);
		return qr;
	}
	
	private Map<String, Set<String>> extractAttributes(SearchParams params) {
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();
		Set<String> iAttributes = new HashSet<String>();
		Set<String> niAttributes = new HashSet<String>();
		String[] types = params.types();
		boolean hasFields = params.hasFields();
		for (int j = 0, jLen = types.length; j < jLen; j++) {
			Map<String, Set<String>> typeAttributes = type_attributes_mapping.get(types[j]);
			Set<String> tiAttributes = typeAttributes.get(I_ATTRIBUTES);
			Set<String> tniAttributes = typeAttributes.get(NI_ATTRIBUTES);
			if (!hasFields) {
				iAttributes.addAll(tiAttributes);
				niAttributes.addAll(tniAttributes);
			} else {
				String[] fields = params.fields();
				for (int k = 0, kLen = fields.length; k < kLen; k++) {
					String field = fields[k];
					if (tiAttributes.contains(field)) {
						iAttributes.add(field);
					}
					if (tniAttributes.contains(field) || (!tiAttributes.contains(field) 
							&& !tniAttributes.contains(field))) {
						niAttributes.add(field);
					}
				}
			}
		}
		attributes.put(I_ATTRIBUTES, iAttributes);
		attributes.put(NI_ATTRIBUTES, niAttributes);
		return attributes;
	}
	
	private BoolQueryBuilder buildBoolQuery(String[] keywords, Map<String, Set<String>> attributes) {
		Set<String> iAttributes = attributes.get(I_ATTRIBUTES);
		Set<String> niAttrbutes = attributes.get(NI_ATTRIBUTES);
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		for (int i = 0, len = keywords.length; i < len; i++) {
			boolQueryBuilder.must(buildBoolQuery(keywords[i], iAttributes, niAttrbutes));
		}
		return boolQueryBuilder;
	}
	
	private BoolQueryBuilder buildBoolQuery(String keyword, Set<String> iAttributes, Set<String> niAttrbutes) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (isIdentity(keyword)) {
			for (String attribute : iAttributes) {
				boolQueryBuilder.should(QueryBuilders.termQuery(attribute, keyword));
			}
		} else {
			for (String attribute : niAttrbutes) {
				boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(attribute, keyword));
			}
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
	
	private void buildHighLight(SearchRequestBuilder searchRequestBuilder, Map<String, Set<String>> attributes) {
		searchRequestBuilder.setHighlighterPreTags("<span style=\"color:red\">");
        searchRequestBuilder.setHighlighterPostTags("</span>");
        Set<String> iAttributes = attributes.get(I_ATTRIBUTES);
		for (String attribute : iAttributes) {
        	searchRequestBuilder.addHighlightedField(attribute);
        }
		Set<String> niAttrbutes = attributes.get(NI_ATTRIBUTES);
		for (String attribute : niAttrbutes) {
			searchRequestBuilder.addHighlightedField(attribute);
		}
	}
	
	private void wrapperHighLight(Map<String, Object> source, Map<String, HighlightField> highLightFields) {
		String entryKey = null;
		Object entryValue = null;
		for (Map.Entry<String, Object> entry : source.entrySet()) {
			entryKey = entry.getKey();
			if (!highLightFields.containsKey(entryKey)) continue;
			Text[] texts = highLightFields.get(entryKey).getFragments();
			StringBuilder highLightText = new StringBuilder(100);
			for (int i = 0, tlen = texts.length; i < tlen; i++) {
				highLightText.append(texts[i]);
			}
			if (highLightText.length() > 0) entryValue = highLightText.toString();
			entry.setValue(entryValue);
		}
	}
	
	private String genCacheKey(SearchParams params) {
		StringBuilder sb = new StringBuilder();
		String[] indices = params.indices();
		for (int i = 0, len = indices.length; i < len; i++) {
			sb.append(indices[i]).append("-");
		}
		String[] types = params.types();
		for (int i = 0, len = types.length; i < len; i++) {
			sb.append(types[i]).append("-");
		}
		if (params.hasFields()) {
			String[] fields = params.fields();
			for (int i = 0, len = fields.length; i < len; i++) {
				sb.append(fields[i]).append("-");
			}
		}
		String[] keywords = params.keywords();
		for (int i = 0, len = keywords.length; i < len; i++) {
			sb.append(keywords[i]).append("-");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return MD5Utils.hash(sb.toString());
	}
	
}
