package org.cisiondata.modules.elastic.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.modules.elastic.entity.SearchParams;
import org.cisiondata.modules.elastic.service.IElastic5Service;
import org.cisiondata.modules.elastic.utils.Elastic5Client;
import org.cisiondata.modules.hbase.service.IHBaseService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.idgen.MD5Utils;
import org.cisiondata.utils.param.ParamsUtils;
import org.cisiondata.utils.redis.RedisClusterUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("elastic5Service")
public class Elastic5ServiceImpl extends Elastic5AbstractServiceImpl implements IElastic5Service {
	
	private Logger LOG = LoggerFactory.getLogger(Elastic5ServiceImpl.class);
	
	@SuppressWarnings("unused")
	private static final String CN_REG = "[\\u4e00-\\u9fa5]+";
	
	private static final String PHONE_REG = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])(\\d{8}|\\*{4}\\d{4}|\\*{5}\\d{3}|\\*{8})$";
	
	private static final String CALL_REG = "0[0-9]{3}-?[2-9][0-9]{6}|0[0-9]{2}-?[2-9][0-9]{7}|400[0-9]{7}|[2-9][0-9]{6,7}";

	private static final String IDCARD_REG = "\\d{17}(\\d|X|x)|\\d{15}";
	
	private static final String EMAIL_REG = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
	
	private static final String QQ_REG = "[1-9][0-9]{4,9}";
	
	private static final String DIGITAL_REG = "^\\d{1,}$";
	
	@Resource(name = "hbaseService")
	private IHBaseService hbaseService = null;
	
	@Override
	public Object readDataList(String indices, String types, String keywords, int highLight,
			Integer currentPageNum, Integer rowNumPerPage) throws BusinessException {
		ParamsUtils.checkNotNull(indices, "indices is null");
		ParamsUtils.checkNotNull(types, "types is null");
		ParamsUtils.checkNotNull(keywords, "keywords is null");
		SearchParams params = new SearchParams(indices, types, keywords, highLight, currentPageNum, rowNumPerPage);
		try {
			QueryResult<Map<String, Object>> qr = readDataList(params);
			boolean isPagination = params.isPagination();
			if (!isPagination) return qr.getResultList();
			return qr.getPaginationResultList(currentPageNum, rowNumPerPage);
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
		SearchRequestBuilder searchRequestBuilder = Elastic5Client.getInstance().getClient()
				.prepareSearch(params.indices()).setTypes(params.types());
		searchRequestBuilder.setQuery(buildBoolQuery(params.keywords()));
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setSize(200);
		searchRequestBuilder.setExplain(false);
		if (params.isHighLight()) buildHighLight(searchRequestBuilder);
		SearchResponse response = searchRequestBuilder.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		Map<String, Object> source = null;
		for (int i = 0, len = hits.length; i < len; i++) {
			SearchHit hit = hits[i];
			source = hit.getSource();
			if (params.isHighLight()) {
				wrapperHighLight(source, hit.getHighlightFields());
			}
			source.putAll(hbaseService.readDataById(hit.getType(), hit.getId()));
			resultList.add(source);
		}
		QueryResult<Map<String, Object>> qr = new QueryResult<>();
		qr.setResultList(resultList);
		qr.setTotalRowNum(response.getHits().getTotalHits());
		RedisClusterUtils.getInstance().set(cacheKey, qr, 600);
		return qr;
	}

	
	private BoolQueryBuilder buildBoolQuery(String[] keywords) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		for (int i = 0, len = keywords.length; i < len; i++) {
			boolQueryBuilder.must(buildBoolQuery(keywords[i]));
		}
		return boolQueryBuilder;
	}
	
	private BoolQueryBuilder buildBoolQuery(String keyword) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (isIdentity(keyword)) {
			for (String attribute : i_attributes) {
				boolQueryBuilder.should(QueryBuilders.termQuery(attribute, keyword));
			}
		} else {
			Set<String> ni_attributes = new HashSet<String>();
			ni_attributes.addAll(n_attributes);
			ni_attributes.addAll(a_attributes);
			ni_attributes.addAll(d_attributes);
			ni_attributes.addAll(o_attributes);
			for (String attribute : ni_attributes) {
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
	
	private void buildHighLight(SearchRequestBuilder searchRequestBuilder) {
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.requireFieldMatch(false);
		highlightBuilder.preTags("<span style=\"color:red\">");
		highlightBuilder.postTags("</span>");
		highlightBuilder.field("*");
		searchRequestBuilder.highlighter(highlightBuilder);
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
		String[] keywords = params.keywords();
		for (int i = 0, len = keywords.length; i < len; i++) {
			sb.append(keywords[i]).append("-");
		}
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return MD5Utils.hash(sb.toString());
	}
	
}
