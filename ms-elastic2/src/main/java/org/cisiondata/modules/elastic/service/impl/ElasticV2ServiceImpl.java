package org.cisiondata.modules.elastic.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.cisiondata.modules.abstr.entity.QueryResult;
import org.cisiondata.modules.elastic.entity.BoolCondition;
import org.cisiondata.modules.elastic.entity.SearchParams;
import org.cisiondata.modules.elastic.entity.TermCondition;
import org.cisiondata.modules.elastic.service.IElasticV2Service;
import org.cisiondata.modules.elastic.utils.ElasticClient;
import org.cisiondata.modules.elastic.utils.ElasticScriptUtils;
import org.cisiondata.modules.elastic.utils.ElasticUtils;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.idgen.MD5Utils;
import org.cisiondata.utils.json.GsonUtils;
import org.cisiondata.utils.param.ParamsUtils;
import org.cisiondata.utils.redis.RedisClusterUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("elasticV2Service")
public class ElasticV2ServiceImpl extends ElasticV2AbstractServiceImpl implements IElasticV2Service {
	
	private Logger LOG = LoggerFactory.getLogger(ElasticV2ServiceImpl.class);
	
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
		QueryResult<Map<String, Object>> qr = readDataList(params);
		if (!params.isPagination()) return qr.getResultList();
		qr.setResultList(qr.getPaginationResultList(currentPageNum, rowNumPerPage));
		return qr;
	}
	
	@Override
	public QueryResult<Map<String, Object>> readDataList(String indices, String types, String fields, 
			String keywords, int highLight, String scrollId) throws BusinessException {
		ParamsUtils.checkNotNull(indices, "indices is null");
		ParamsUtils.checkNotNull(types, "types is null");
		ParamsUtils.checkNotNull(keywords, "keywords is null");
		SearchParams params = new SearchParams(indices, types, fields, keywords, highLight, scrollId);
		return readDataList(params);
	}
	
	@SuppressWarnings("unchecked")
	private QueryResult<Map<String, Object>> readDataList(SearchParams params) throws BusinessException {
		String cacheKey = genCacheKey(params);
		try {
			Object cacheObject = RedisClusterUtils.getInstance().get(cacheKey);
			if (null != cacheObject) {
				return (QueryResult<Map<String, Object>>) cacheObject;
			} 
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		QueryResult<Map<String, Object>> qr = new QueryResult<Map<String, Object>>();
		String[] indicesTypes = extractIndicesAndTypes(params.indices(), params.types());
		if (indicesTypes.length < 2) throw new BusinessException("indices types error!");
		for (int i = 0, len = indicesTypes.length; i < len; i = i + 2) {
			QueryResult<Map<String, Object>> subqr = params.isJson() ? 
				readDataListWithJson(indicesTypes[i], indicesTypes[i+1], params) :
					readDataList(indicesTypes[i], indicesTypes[i+1], params);
			qr.getResultList().addAll(subqr.getResultList());
			qr.setTotalRowNum(qr.getTotalRowNum() + subqr.getTotalRowNum());
		}
		try {
			RedisClusterUtils.getInstance().set(cacheKey, qr, 600);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
		return qr;
	}
	
	private QueryResult<Map<String, Object>> readDataListWithJson(String index, String type, SearchParams params) {
		try {
			SearchRequestBuilder searchRequestBuilder = ElasticClient.getInstance().getClient()
					.prepareSearch(index).setTypes(type);
			Set<String> attributes = new HashSet<String>();
			searchRequestBuilder.setQuery(buildQuery(params.getKeywords(), attributes));
			searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
			searchRequestBuilder.setSize(200);
			searchRequestBuilder.setExplain(false);
			if (params.isHighLight()) buildHighLight(searchRequestBuilder, attributes);
			return buildQueryResult(searchRequestBuilder, params);
		} catch (Exception e) {
			LOG.error("index: {} type: {} query: {}", index, type, params.getKeywords());
			LOG.error(e.getMessage(), e);
		}
		return new QueryResult<Map<String, Object>>();
	}
	
	private QueryResult<Map<String, Object>> readDataList(String index, String type, SearchParams params) {
		try {
			Map<String, Set<String>> attributes = extractAttributes(index, type, params);
			if (attributes.size() == 0) return new QueryResult<Map<String, Object>>();
			SearchRequestBuilder searchRequestBuilder = ElasticClient.getInstance().getClient()
					.prepareSearch(index).setTypes(type);
			searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
			/**
			ScoreFunctionBuilder scoreFunctionBuilder = 
			ScoreFunctionBuilders.fieldValueFactorFunction(fieldName)
				.factor(boostFactor).modifier(modifier).missing(missing).setWeight(weight);
			线性衰减函数  一旦直线与横轴 0 相交，所有其他值的评分都是 0.0
			ScoreFunctionBuilders.linearDecayFunction(fieldName, origin, scale)
				.setDecay(decay).setOffset(offset).setWeight(weight)
			指数衰减函数  先剧烈衰减然后变缓 
			ScoreFunctionBuilders.exponentialDecayFunction(fieldName, origin, scale)
				.setDecay(decay).setOffset(offset).setWeight(weight)
			高斯衰减函数  高斯函数是钟形的——它的衰减速率是先缓慢，然后变快，最后又放缓。
			ScoreFunctionBuilders.gaussDecayFunction(fieldName, origin, scale)
				.setDecay(decay).setOffset(offset).setWeight(weight)
			origin  中心点或字段可能的最佳值，落在原点 origin上的文档评分 _score 为满分 1.0 。
			scale  衰减率，即一个文档从原点 origin下落时，评分 _score改变的速度。
			decay  从原点 origin衰减到 scale所得的评分 _score，默认值为 0.5 。
			offset  以原点 origin为中心点，为其设置一个非零的偏移量 offset覆盖一个范围，而不只是单个原点。
				在范围 -offset <= origin <= +offset内的所有评分 _score 都是 1.0 。
			随机评分
			ScoreFunctionBuilders.randomFunction(seed)
			权重因素
			ScoreFunctionBuilders.weightFactorFunction(weight)
			**/
			String timeField = getTypeTimeField(type);
			if (StringUtils.isBlank(timeField)) {
				searchRequestBuilder.setQuery(buildBoolQuery(params.keywords(), attributes));
			} else {
				String inlineScript = ElasticScriptUtils.scriptWithScoreAndTime(timeField, System.currentTimeMillis());
				Map<String, Object> sparams = new HashMap<>();  
				Script script = new Script(inlineScript, ScriptType.INLINE, "groovy", sparams);  
				ScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.scriptFunction(script);  
				searchRequestBuilder.setQuery(QueryBuilders.functionScoreQuery(
						buildBoolQuery(params.keywords(), attributes), scoreFunctionBuilder));
			}
			/**
			searchRequestBuilder.setScroll(TimeValue.timeValueMinutes(3));
			*/
			searchRequestBuilder.setSize(200);
			searchRequestBuilder.setExplain(false);
			if (params.isHighLight()) buildHighLight(searchRequestBuilder, attributes);
			return buildQueryResult(searchRequestBuilder, params);
		} catch (Exception e) {
			LOG.error("index: {} type: {} query: {}", index, type, params.getKeywords());
			LOG.error(e.getMessage(), e);
		}
		return new QueryResult<Map<String, Object>>();
	}
	
	private Map<String, Set<String>> extractAttributes(String index, String type, SearchParams params) {
		Map<String, Set<String>> typeAttributes = type_attributes_mapping.get(index + "_" + type);
		if (!params.isFields()) return typeAttributes;
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();
		Set<String> iAttributes = new HashSet<String>();
		Set<String> niAttributes = new HashSet<String>();
		Set<String> tiAttributes = typeAttributes.get(I_ATTRIBUTES);
		Set<String> tniAttributes = typeAttributes.get(NI_ATTRIBUTES);
		String[] fields = params.fields();
		for (int i = 0, len = fields.length; i < len; i++) {
			String field = fields[i];
			if (tiAttributes.contains(field)) {
				iAttributes.add(field);
			}
			if (tniAttributes.contains(field) || (!tiAttributes.contains(field) 
					&& !tniAttributes.contains(field))) {
				niAttributes.add(field);
			}
		}
		if (iAttributes.size() > 0) attributes.put(I_ATTRIBUTES, iAttributes);
		if (niAttributes.size() > 0) attributes.put(NI_ATTRIBUTES, niAttributes);
		return attributes;
	}
	
	private QueryBuilder buildQuery(String json, Set<String> attributes) {
		if (!json.contains("Clauses")) {
			TermCondition termCondition = GsonUtils.builder().fromJson(json, TermCondition.class);
			return buildTermQuery(termCondition, attributes);
		} else {
			BoolCondition boolCondition = GsonUtils.builder().fromJson(json, BoolCondition.class);
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			buildBoolQueryBuilder(boolCondition, boolQueryBuilder, attributes);
			return boolQueryBuilder;
		}
	}
	
	private QueryBuilder buildTermQuery(TermCondition termCondition, Set<String> attributes) {
		String name = termCondition.getName();
		attributes.add(name);
		Object value = termCondition.getValue();
		String valueString = String.valueOf(value);
		QueryBuilder queryBuilder = null;
		if (isIdentity(valueString)) {
			queryBuilder = QueryBuilders.termQuery(name, value);
		} else if (isChinese(valueString)) {
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
			String[] keywords = ElasticUtils.convertSynonyms(valueString);
			for (int i = 0, len = keywords.length; i < len; i++) {
				boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(name, keywords[i]));
			}
			queryBuilder = boolQueryBuilder;
		} else {
			queryBuilder = QueryBuilders.matchPhraseQuery(name, value);
		}
		return queryBuilder;
	}
	
	private void buildBoolQueryBuilder(BoolCondition boolCondition, BoolQueryBuilder 
			boolQueryBuilder, Set<String> attributes) {
		if (!boolCondition.hasClauses()) return;
		boolCondition.getMustClauses().forEach(condition -> {
			if (condition instanceof TermCondition) {
				boolQueryBuilder.must(buildTermQuery((TermCondition) condition, attributes));
			} else if (condition instanceof BoolCondition) {
				BoolQueryBuilder subBoolQueryBuilder = new BoolQueryBuilder();
				buildBoolQueryBuilder((BoolCondition) condition, subBoolQueryBuilder, attributes);
				boolQueryBuilder.must(subBoolQueryBuilder);
			}
		});
		boolCondition.getMustNotClauses().forEach(condition -> {
			if (condition instanceof TermCondition) {
				boolQueryBuilder.mustNot(buildTermQuery((TermCondition) condition, attributes));
			} else if (condition instanceof BoolCondition) {
				BoolQueryBuilder subBoolQueryBuilder = new BoolQueryBuilder();
				buildBoolQueryBuilder((BoolCondition) condition, subBoolQueryBuilder, attributes);
				boolQueryBuilder.mustNot(subBoolQueryBuilder);
			}
		});
		boolCondition.getFilterClauses().forEach(condition -> {
			if (condition instanceof TermCondition) {
				boolQueryBuilder.filter(buildTermQuery((TermCondition) condition, attributes));
			} else if (condition instanceof BoolCondition) {
				BoolQueryBuilder subBoolQueryBuilder = new BoolQueryBuilder();
				buildBoolQueryBuilder((BoolCondition) condition, subBoolQueryBuilder, attributes);
				boolQueryBuilder.filter(subBoolQueryBuilder);
			}
		});
		boolCondition.getShouldClauses().forEach(condition -> {
			if (condition instanceof TermCondition) {
				boolQueryBuilder.should(buildTermQuery((TermCondition) condition, attributes));
			} else if (condition instanceof BoolCondition) {
				BoolQueryBuilder subBoolQueryBuilder = new BoolQueryBuilder();
				buildBoolQueryBuilder((BoolCondition) condition, subBoolQueryBuilder, attributes);
				boolQueryBuilder.should(subBoolQueryBuilder);
			}
		});
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
		} else if (isChinese(keyword)) {
			String[] keywords = ElasticUtils.convertSynonyms(keyword);
			for (int i = 0, len = keywords.length; i < len; i++) {
				for (String attribute : niAttrbutes) {
					boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(attribute, keywords[i]));
				}
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
	
	private boolean isChinese(String keyword) {
		return isMatchRegex(keyword, CN_REG);
	}
	
	private boolean isMatchRegex(String keyword, String regex) {
		return Pattern.compile(regex).matcher(keyword).find();
	}
	
	private QueryResult<Map<String, Object>> buildQueryResult(SearchRequestBuilder searchRequestBuilder, 
			SearchParams params) {
		SearchResponse response = searchRequestBuilder.execute().actionGet();
		/**
		LOG.info("scrollId: " + response.getScrollId());
		if (params.isScroll()) {
			response = ElasticClient.getInstance().getClient().prepareSearchScroll(
				params.getScrollId()).setScroll(TimeValue.timeValueMinutes(3)).execute().actionGet();
		}
		*/
		QueryResult<Map<String, Object>> qr = new QueryResult<Map<String, Object>>();
		SearchHit[] hits = response.getHits().getHits();
		Map<String, Object> source = null;
		for (int i = 0, len = hits.length; i < len; i++) {
			SearchHit hit = hits[i];
			source = hit.getSource();
			source.put("score", hit.getScore());
			if (params.isHighLight()) wrapperHighLight(source, hit.getHighlightFields());
			qr.getResultList().add(source);
		}
		qr.setTotalRowNum(response.getHits().getTotalHits());
		return qr;
	}
	
	private void buildHighLight(SearchRequestBuilder searchRequestBuilder, Set<String> attributes) {
		searchRequestBuilder.setHighlighterPreTags("<span style=\"color:red\">");
        searchRequestBuilder.setHighlighterPostTags("</span>");
		for (String attribute : attributes) {
        	searchRequestBuilder.addHighlightedField(attribute);
        }
	}
	
	private void buildHighLight(SearchRequestBuilder searchRequestBuilder, Map<String, Set<String>> attributes) {
		searchRequestBuilder.setHighlighterPreTags("<span style=\"color:red\">");
        searchRequestBuilder.setHighlighterPostTags("</span>");
        if (attributes.containsKey(I_ATTRIBUTES)) {
        	for (String attribute : attributes.get(I_ATTRIBUTES)) {
        		searchRequestBuilder.addHighlightedField(attribute);
        	}
        }
        if (attributes.containsKey(NI_ATTRIBUTES)) {
        	for (String attribute : attributes.get(NI_ATTRIBUTES)) {
        		searchRequestBuilder.addHighlightedField(attribute);
        	}
        }
	}
	
	private void wrapperHighLight(Map<String, Object> source, Map<String, HighlightField> highLightFields) {
		String entryKey = null;
		for (Map.Entry<String, Object> entry : source.entrySet()) {
			entryKey = entry.getKey();
			if (!highLightFields.containsKey(entryKey)) continue;
			Text[] texts = highLightFields.get(entryKey).getFragments();
			StringBuilder highLightText = new StringBuilder(100);
			for (int i = 0, tlen = texts.length; i < tlen; i++) {
				highLightText.append(texts[i]);
			}
			if (highLightText.length() > 0) {
				entry.setValue(highLightText.toString());
			}
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
		if (params.isFields()) {
			String[] fields = params.fields();
			for (int i = 0, len = fields.length; i < len; i++) {
				sb.append(fields[i]).append("-");
			}
		}
		String[] keywords = params.keywords();
		for (int i = 0, len = keywords.length; i < len; i++) {
			sb.append(keywords[i]).append("-");
		}
		sb.append(params.isHighLight() ? 1 : 0).append("-");
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
		return MD5Utils.hash(sb.toString());
	}
	
}
