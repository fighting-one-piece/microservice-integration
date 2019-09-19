package org.platform.modules.elastic.utils;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticRestClientHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(ElasticRestClientHelper.class);
	
	public static String search(QueryBuilder queryBuilder, int from, int size, String... indices) {
		RestHighLevelClient restHighLevelClient = ElasticRestClient.getInstance().getClient();
		SearchRequest searchRequest = new SearchRequest(indices);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(queryBuilder);
		searchSourceBuilder.from(from).size(size);
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.preTags("<span style=\"color:red\">");
		highlightBuilder.postTags("</span>");
		highlightBuilder.field("*");
		searchSourceBuilder.highlighter(highlightBuilder);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueMinutes(5));
		searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		LOG.info("took {}", searchResponse.getTook().getSeconds());
		SearchHits searchHits = searchResponse.getHits();
		long totalHits = searchHits.getTotalHits().value;
		LOG.info("total hits {}", totalHits);
		SearchHit[] searchHitArray = searchHits.getHits();
		for (int i = 0, len = searchHitArray.length; i < len; i++) {
			SearchHit searchHit = searchHitArray[i];
			Map<String, Object> source = searchHit.getSourceAsMap();
			wrapperHighLight(source, searchHit.getHighlightFields());
			LOG.info("{} {} {}", searchHit.getScore(), searchHit.getId(), source.get("title"));
		}
		try {
			restHighLevelClient.close();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return searchResponse.getScrollId();
	}
	
	private static void wrapperHighLight(Map<String, Object> source, Map<String, HighlightField> highLightFields) {
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
	
	/** Match查询匹配分词后的关键字中任意一个以上 */
	public static void search_01() {
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "周杰伦、罗志祥");
		search(matchQueryBuilder, 0, 10, "article");
	}
	
	/** 短语查询匹配分词后的关键字中每一个且邻接有序 */
	public static void search_02() {
		MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "周杰伦、罗志祥");
		search(matchPhraseQueryBuilder, 0, 10, "article");
	}
	
	/** 短语查询匹配分词后的关键字中每一个且有序、间隔可以邻接或为1 */
	public static void search_03() {
		MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "周杰伦、罗志祥");
		matchPhraseQueryBuilder.slop(1);
		search(matchPhraseQueryBuilder, 0, 10, "article");
	}
	
	/** 短语查询匹配分词后的关键字中每一个且有序、间隔可以邻接或为10以内 */
	public static void search_04() {
		MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "周杰伦、罗志祥");
		matchPhraseQueryBuilder.slop(10);
		search(matchPhraseQueryBuilder, 0, 10, "article");
	}
	
	/** 在Match查询的基础上通过短语查询提升相关度分值 */
	public static void search_05() {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "周杰伦、罗志祥");
		boolQueryBuilder.must(matchQueryBuilder);
		MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "周杰伦、罗志祥");
		matchPhraseQueryBuilder.slop(1);
		boolQueryBuilder.should(matchPhraseQueryBuilder);
		search(boolQueryBuilder, 0, 10, "article");
	}
	
	/** 在Match查询的基础上通过boost值来控制每个查询子句的相对权重，提升相关度分值 */
	public static void search_06() {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("title", "周杰伦、林俊杰、罗志祥");
		matchQueryBuilder1.minimumShouldMatch("30%");  
		boolQueryBuilder.must(matchQueryBuilder1);
		MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("title", "周杰伦");
		matchQueryBuilder2.boost(5.0f);
		boolQueryBuilder.should(matchQueryBuilder2);
		MatchQueryBuilder matchQueryBuilder3 = QueryBuilders.matchQuery("title", "林俊杰");
		matchQueryBuilder3.boost(3.0f);
		boolQueryBuilder.should(matchQueryBuilder3);
		search(boolQueryBuilder, 0, 10, "article");
	}
	
	/** 在Match查询的基础上通过shingle关联词提升相关度分值 */
	/** shingle索引时创建,比短语查询灵活、性能高,需要选择合适的shingle_size */
	public static void search_07() {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("title", "周杰伦、林俊杰、罗志祥");
		matchQueryBuilder1.minimumShouldMatch("30%");
		boolQueryBuilder.must(matchQueryBuilder1);
		MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("title.shingle", "周杰伦、罗志祥");
		boolQueryBuilder.should(matchQueryBuilder2);
		search(boolQueryBuilder, 0, 10, "article");
	}
	
}
