package org.platform.modules.elastic.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.SortOrder;
import org.platform.modules.abstr.entity.QueryResult;
import org.platform.modules.elastic.service.IElasticV2Service;
import org.platform.modules.elastic.service.impl.ElasticV2ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLookupContainer;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;

public class ElasticClientHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(ElasticClientHelper.class);
	
	/**
	 * 创建索引
	 * @param index
	 * @param shardsNum 分片数
	 * @param replicasNum 备份数
	 */
	public static void createIndex(String index, String shardsNum, String replicasNum) {
		Client client = ElasticClient.getInstance().getClient();
		try {
			XContentBuilder builder = XContentFactory
			            .jsonBuilder()
			            .startObject()
		                    .field("number_of_shards", shardsNum)
		                    .field("number_of_replicas", replicasNum)
				        .endObject();
			CreateIndexResponse response = client.admin().indices()
					.prepareCreate(index).setSettings(builder).execute().actionGet();
			System.out.println(response.isAcknowledged());
		} catch (Exception e) {
			LOG.error("create index error.", e);
		} 
	}
	
	/**
	 * 删除索引
	 * @param index
	 */
	public static void deleteIndex(String index) {
		Client client = ElasticClient.getInstance().getClient();
		try {
			DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
			ActionFuture<DeleteIndexResponse> response = 
					client.admin().indices().delete(deleteIndexRequest);
			System.out.println(response.get().isAcknowledged());
		} catch (Exception e) {
			LOG.error("delete index error.", e);
		} 
	}
	
	/**
	 * 创建索引类型表
	 * @param index
	 * @param type
	 * @param builder
	 */
	public static void createIndexType(String index, String type, XContentBuilder builder) {
		Client client = ElasticClient.getInstance().getClient();
		PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
		PutMappingResponse response = client.admin().indices().putMapping(mapping).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	/**
	 * 根据预先定义好的mapping文件创建索引类型表
	 * @param index
	 * @param type
	 * @param fileName
	 */
	public static void createIndexType(String index, String type, String fileName) {
		Client client = ElasticClient.getInstance().getClient();
		PutMappingRequest mapping = Requests.putMappingRequest(index)
				.type(type).source(readSource(fileName));
		PutMappingResponse response = client.admin().indices().putMapping(mapping).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	/**
	 * 根据文件名称读取mapping文件
	 * @param fileName
	 * @return
	 */
	private static String readSource(String fileName) {
		InputStream in = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			in = ElasticClientHelper.class.getClassLoader().getResourceAsStream("mapping/" + fileName);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while (null != (line = br.readLine())) {
				sb.append(line);
			}
		} catch (Exception e) {
			LOG.error("read source error.", e);
		} finally {
			try {
				if (null != br) {
					br.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {
				LOG.error("close reader or stream error.", e);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 删除索引类型表所有数据，批量删除
	 * @param index
	 * @param type
	 */
	public static void deleteIndexTypeAllData(String index, String type) {
		Client client = ElasticClient.getInstance().getClient();
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setScroll(new TimeValue(60000)).setSize(10000).setExplain(false).execute().actionGet();
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		while (true) {
			SearchHit[] hitArray = response.getHits().getHits();
			SearchHit hit = null;
			for (int i = 0, len = hitArray.length; i < len; i++) {
				hit = hitArray[i];
				DeleteRequestBuilder request = client.prepareDelete(index, type, hit.getId());
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
	}
	
	/**
	 * 删除索引类型表所有数据，定制批量删除
	 * @param index
	 * @param type
	 */
	public static void deleteIndexTypeAllDataWithProcessor(String index, String type) {
		Client client = ElasticClient.getInstance().getClient();
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery()).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setScroll(new TimeValue(60000)).setSize(10000).setExplain(false).execute().actionGet();
		BulkProcessor.Listener listener = new BulkProcessor.Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				LOG.info("request actions num {}", request.numberOfActions());
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request,
					Throwable failure) {
				LOG.error(failure.getMessage());
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request,
					BulkResponse response) {
				if (response.hasFailures()) {
					LOG.error(response.buildFailureMessage());
				}
			}
		};
		BulkProcessor bulkProcessor = BulkProcessor.builder(client, listener)
				.setBulkActions(10000)
				.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)) 
				.setFlushInterval(TimeValue.timeValueSeconds(5)) 
				.setConcurrentRequests(1) 
				.setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)) 
				.build();
		while (true) {
			SearchHit[] hitArray = response.getHits().getHits();
			SearchHit hit = null;
			for (int i = 0, len = hitArray.length; i < len; i++) {
				hit = hitArray[i];
				DeleteRequestBuilder request = client.prepareDelete(index, type, hit.getId());
				bulkProcessor.add(request.request());
			}
			if (hitArray.length == 0) break;
			response = client.prepareSearchScroll(response.getScrollId())
							.setScroll(new TimeValue(60000)).execute().actionGet();
		}
		try {
			bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据ID删除索引类型表数据
	 * @param index
	 * @param type
	 * @param id
	 */
	public static void deleteIndexTypeDataById(String index, String type, String id) {
		Client client = ElasticClient.getInstance().getClient();
		DeleteResponse response = client.prepareDelete().setIndex(index)
				.setType(type).setId(id).execute().actionGet();
		System.out.println(response.isFound());
	}
	
	/**
	 * 根据条件删除索引类型表数据
	 * @param index
	 * @param type
	 * @param query
	 */
	public static void deleteIndexTypeDatasByQuery(String index, String type, QueryBuilder query) {
		Client client = ElasticClient.getInstance().getClient();
		SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(query)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setScroll(new TimeValue(60000))
					.setSize(1000).setExplain(false).execute().actionGet();
		LOG.info("total hits: {}", response.getHits().getTotalHits());
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		while (true) {
			SearchHit[] hitArray = response.getHits().getHits();
			SearchHit hit = null;
			for (int i = 0, len = hitArray.length; i < len; i++) {
				hit = hitArray[i];
				DeleteRequestBuilder request = client.prepareDelete(index, type, hit.getId());
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
	}
	
	/**
	 * 根据条件读取索引类型表数据
	 * @param index
	 * @param type
	 * @param query
	 * @return
	 */
	public static List<Map<String, Object>> readIndexTypeDatasByQuery(String index, String type, QueryBuilder query) {
		Client client = ElasticClient.getInstance().getClient();
		SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(query)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setScroll(new TimeValue(60000))
					.setSize(1000).setExplain(false).execute().actionGet();
		LOG.info("total hits: " + response.getHits().getTotalHits());
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		while (true) {
			SearchHit[] hitArray = response.getHits().getHits();
			for (int i = 0, len = hitArray.length; i < len; i++) {
				datas.add(hitArray[i].getSource());
			}
			if (hitArray.length == 0) break;
			response = client.prepareSearchScroll(response.getScrollId())
							.setScroll(new TimeValue(60000)).execute().actionGet();
		}
		return datas;
	}
	
	/**
	 * 根据条件读取索引类型表数据分页信息
	 * @param index
	 * @param type
	 * @param query
	 * @param scrollId
	 * @param size
	 * @return
	 */
	public static List<Map<String, Object>> readIndexTypeDatasByQueryWithPagination(String index, String type, 
			QueryBuilder query, String scrollId, int size) {
		Client client = ElasticClient.getInstance().getClient();
		SearchResponse response = client.prepareSearch(index).setTypes(type).setQuery(query)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setScroll(TimeValue.timeValueMinutes(3))
					.setSize(size).setExplain(false).execute().actionGet();
		LOG.info("total hits: " + response.getHits().getTotalHits());
		if (StringUtils.isNotBlank(scrollId)) {
			response = client.prepareSearchScroll(scrollId).setScroll(
					TimeValue.timeValueMinutes(3)).execute().actionGet();
		} 
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SearchHit[] hitArray = response.getHits().getHits();
		for (int i = 0, len = hitArray.length; i < len; i++) {
			datas.add(hitArray[i].getSource());
		}
		return datas;
	}
	
	/**
	 * 根据列名读取索引类型表的分组信息
	 * @param index
	 * @param type
	 * @param groupFieldName
	 */
	public static void readIndexTypeDatasWithGroup(String index, String type, String groupFieldName) {
		String groupFieldAgg = groupFieldName + "Agg";
		Client client = ElasticClient.getInstance().getClient();
		TermsBuilder termsBuilder = AggregationBuilders.terms(groupFieldAgg)
				.size(Integer.MAX_VALUE).field(groupFieldName);
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
					.addAggregation(termsBuilder).execute().actionGet();
		Terms terms = response.getAggregations().get(groupFieldAgg);
		if (null != terms) {
			List<Bucket> buckets = terms.getBuckets();
			for (int i = 0, len = buckets.size(); i < len; i++) {
				Bucket bucket = buckets.get(i);
				System.out.println(bucket.getKey() + " - " + bucket.getDocCount());
			}
		}
	}
	
	/**
	 * 根据列名读取索引类型表的分组信息，二次分组
	 * @param index
	 * @param type
	 * @param groupFieldName
	 * @param subGroupFieldName
	 */
	public static void readIndexTypeDatasWithGroup(String index, String type, String groupFieldName,
			String subGroupFieldName) {
		String groupFieldAgg = groupFieldName + "Agg";
		String subGroupFieldAgg = subGroupFieldName + "Agg";
		Client client = ElasticClient.getInstance().getClient();
		TermsBuilder subTermsBuilder = AggregationBuilders.terms(subGroupFieldAgg)
				.size(Integer.MAX_VALUE).field(subGroupFieldName);
		TermsBuilder termsBuilder = AggregationBuilders.terms(groupFieldAgg)
				.size(Integer.MAX_VALUE).field(groupFieldName).subAggregation(subTermsBuilder);
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
					.addAggregation(termsBuilder).execute().actionGet();
		Terms terms = response.getAggregations().get(groupFieldAgg);
		if (null != terms) {
			List<Bucket> buckets = terms.getBuckets();
			for (int i = 0, len = buckets.size(); i < len; i++) {
				Bucket bucket = buckets.get(i);
				System.out.println(bucket.getKey() + " - " + bucket.getDocCount());
				Terms subTerms = bucket.getAggregations().get(subGroupFieldAgg);
				if (null != subTerms) {
					List<Bucket> subBuckets = subTerms.getBuckets();
					for (int j = 0, slen = subBuckets.size(); j < slen; j++) {
						Bucket subBucket = subBuckets.get(j);
						System.out.println(subBucket.getKey() + " - " + subBucket.getDocCount());
					}
				}
			}
		}
	}
	
	/**
	 * 读取索引类型表指定列名的平均值
	 * @param index
	 * @param type
	 * @param avgField
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static double readIndexTypeFieldValueWithAvg(String index, String type, String avgField) {
		Client client = ElasticClient.getInstance().getClient();
		String avgName = avgField + "Avg";
		MetricsAggregationBuilder aggregation = AggregationBuilders.avg(avgName).field(avgField);
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
					.addAggregation(aggregation).execute().actionGet();
		Avg avg = response.getAggregations().get(avgName);  
		return avg.getValue();
	}
	
	/**
	 * 读取索引类型表指定列名的总和
	 * @param index
	 * @param type
	 * @param sumField
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static double readIndexTypeFieldValueWithSum(String index, String type, String sumField) {
		Client client = ElasticClient.getInstance().getClient();
		String sumName = sumField + "Sum";
		MetricsAggregationBuilder aggregation = AggregationBuilders.sum(sumName).field(sumField);
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
					.addAggregation(aggregation).execute().actionGet();
		Sum sum = response.getAggregations().get("priceSum");  
		return sum.getValue();  
	}
	
	/**
	 * 读取指定索引指定类型表的总记录数
	 * @param indices
	 * @param types
	 * @return
	 */
	public static long readIndicesTypesDatasCount(String[] indices, String types) {
		Client client= ElasticClient.getInstance().getClient();
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indices).setTypes(types);
		searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
		searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
		SearchResponse response = searchRequestBuilder.execute().actionGet();
		return response.getHits().getTotalHits();
	}
	
	/**
	 * 读取索引元数据信息
	 */
	@SuppressWarnings("unchecked")
	public static void readIndicesTypesMappingsMetadata() {
		try {
			Client client= ElasticClient.getInstance().getClient();
			IndicesAdminClient indicesAdminClient = client.admin().indices();
			GetMappingsResponse getMappingsResponse = indicesAdminClient.getMappings(new GetMappingsRequest()).get();
			ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = 
					getMappingsResponse.getMappings();
			Iterator<ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>>> 
			mappingIterator = mappings.iterator();
			while (mappingIterator.hasNext()) {
				ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>>
				objectObjectCursor = mappingIterator.next();
				LOG.info("index: {}", objectObjectCursor.key);
				ImmutableOpenMap<String, MappingMetaData> immutableOpenMap = objectObjectCursor.value;
				ObjectLookupContainer<String> keys = immutableOpenMap.keys();
				Iterator<ObjectCursor<String>> keysIterator = keys.iterator();
				while(keysIterator.hasNext()) {
					String type = keysIterator.next().value;
					LOG.info("type: {}", type);
					MappingMetaData mappingMetaData = immutableOpenMap.get(type);
					Map<String, Object> mapping = mappingMetaData.getSourceAsMap();
					if (mapping.containsKey("properties")) {
						Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
						for (String attribute : properties.keySet()) {
							LOG.info("attribute: {}", attribute);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	/**
	 * 分词
	 * @param index
	 * @param text
	 */
	public static void analyze(String index, String text) {
		Client client = ElasticClient.getInstance().getClient();
		AnalyzeRequestBuilder request = new AnalyzeRequestBuilder(client, AnalyzeAction.INSTANCE, index, text);
		request.setAnalyzer("ik");
		List<AnalyzeToken> analyzeTokens = request.execute().actionGet().getTokens();
		for (int i = 0, len = analyzeTokens.size(); i < len; i++) {
			AnalyzeToken analyzeToken = analyzeTokens.get(i);
			System.out.println(analyzeToken.getTerm());
		}
	}
	
	public static void test01() {
		Client client = ElasticClient.getInstance().getClient();
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch("financial").setTypes("logistics");
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("city", "成都"));
		boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("orderTime", "2015/1"));
//		boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("orderTime", "2015-1"));
		boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("endTime", "2015/1"));
//		boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("endTime", "2015-1"));
//		boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("sourceFile", "台湾"));
		searchRequestBuilder.setQuery(boolQueryBuilder);
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setSize(1000).setExplain(false);
		searchRequestBuilder.addSort("updateTime", SortOrder.DESC);
		SearchResponse response = searchRequestBuilder.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0, len = hits.length; i < len; i++) {
			System.out.println(hits[i].getSource());
		}
	}
	
	public static void test02() {
		List<String> qqNumList1 = new ArrayList<String>();
		long qqNum1 = 561243625L;
		for (int i = 0; i < 100; i++) {
			qqNumList1.add(String.valueOf(qqNum1 + i));
		}
		
		List<String> qqNumList2 = new ArrayList<String>();
		long qqNum2 = 471246431L;
		for (int i = 0; i < 100; i++) {
			qqNumList2.add(String.valueOf(qqNum2 + i));
		}
		
		List<String> qqNumList3 = new ArrayList<String>();
		long qqNum3 = 621247490;
		for (int i = 0; i < 100; i++) {
			qqNumList3.add(String.valueOf(qqNum3 + i));
		}
		
		Client client = ElasticClient.getInstance().getClient();
		long startTime1 = System.currentTimeMillis();
		MultiSearchRequestBuilder msrb = client.prepareMultiSearch();
		for (int i = 0, len = qqNumList1.size(); i < len; i++) {
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch("qq").setTypes("qqdata");
			searchRequestBuilder.setQuery(QueryBuilders.termQuery("qqNum", qqNumList1.get(i)));
			searchRequestBuilder.setSearchType(SearchType.QUERY_AND_FETCH);
			searchRequestBuilder.setSize(100).setExplain(false);
			msrb.add(searchRequestBuilder);
		}
		MultiSearchResponse msr = msrb.execute().actionGet();
		Item[] items = msr.getResponses();
//		System.out.println("items size: " + items.length);
		for (int i = 0, len = items.length; i < len; i++) {
			Item item = items[i];
			SearchResponse sr = item.getResponse();
			System.out.println("total hits: " + sr.getHits().getTotalHits());
		}
		long endTime1 = System.currentTimeMillis();
		System.out.println("multi query spend time " + (endTime1 - startTime1) / 1000);
		long startTime2 = System.currentTimeMillis();
		for (int i = 0, len = qqNumList2.size(); i < len; i++) {
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch("qq").setTypes("qqdata");
			searchRequestBuilder.setQuery(QueryBuilders.termQuery("qqNum", qqNumList2.get(i)));
			searchRequestBuilder.setSearchType(SearchType.QUERY_AND_FETCH);
			searchRequestBuilder.setSize(100).setExplain(false);
			SearchResponse sr = searchRequestBuilder.execute().actionGet();
			System.out.println("total hits: " + sr.getHits().getTotalHits());
		}
		long endTime2 = System.currentTimeMillis();
		System.out.println("single query spend time " + (endTime2 - startTime2) / 1000);
		long startTime3 = System.currentTimeMillis();
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch("qq").setTypes("qqdata");
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		for (int i = 0, len = qqNumList3.size(); i < len; i++) {
			queryBuilder.should(QueryBuilders.termQuery("qqNum", qqNumList3.get(i)));
		}
		searchRequestBuilder.setQuery(queryBuilder);
		searchRequestBuilder.setSearchType(SearchType.QUERY_AND_FETCH);
		searchRequestBuilder.setSize(100).setExplain(false);
		SearchResponse sr = searchRequestBuilder.execute().actionGet();
		System.out.println("total hits: " + sr.getHits().getTotalHits());
		long endTime3 = System.currentTimeMillis();
		System.out.println("one query spend time " + (endTime3 - startTime3) / 1000);
		client.close();
	}
	
	public static String test03(String scrollId) {
		SearchRequestBuilder searchRequestBuilder = ElasticClient.getInstance().getClient()
				.prepareSearch("financial").setTypes("logistics");
		searchRequestBuilder.setExplain(false);
		searchRequestBuilder.setQuery(QueryBuilders.wildcardQuery("name", "*王*"));
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setScroll(TimeValue.timeValueMinutes(3));
		searchRequestBuilder.setSize(5);
		SearchResponse response = searchRequestBuilder.execute().actionGet();
		LOG.info("scrollId: " + response.getScrollId());
		if (StringUtils.isNotBlank(scrollId)) {
			response = ElasticClient.getInstance().getClient().prepareSearchScroll(scrollId)
					.setScroll(TimeValue.timeValueMinutes(3)).execute().actionGet();
		}
		System.err.println("total hits: " + response.getHits().getTotalHits());
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0, len = hits.length; i < len; i++) {
			SearchHit hit = hits[i];
			System.err.println(hit.getSource());
		}
		return response.getScrollId();
	}
	
	public static void test04(String scrollId) {
		IElasticV2Service elasticService = new ElasticV2ServiceImpl();
		QueryResult<Map<String, Object>> qr = elasticService.readDataList("financial", "logistics", null, "张", 0, scrollId);
		System.err.println("total hits: " + qr.getTotalRowNum());
		for (Map<String, Object> map : qr.getResultList()) {
			System.err.println(map);
		}
	}
	
	public static void main(String[] args) {
		test03(null);
//		test03("cXVlcnlUaGVuRmV0Y2g7MTA7MzMwNzI6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3MzpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDc1OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwNzY6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3NzpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDc0OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwNzg6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3OTpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDgwOlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwODE6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTswOw");
//		test03("cXVlcnlUaGVuRmV0Y2g7MTA7MzMwNzI6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3MzpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDc1OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwNzY6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3NzpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDc0OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwNzg6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3OTpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDgwOlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwODE6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTswOw");
//		test03("cXVlcnlUaGVuRmV0Y2g7MTA7MzMwNzI6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3MzpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDc1OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwNzY6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3NzpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDc0OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwNzg6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzA3OTpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMDgwOlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMwODE6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTswOw");
//		test04(null);
//		test04("cXVlcnlUaGVuRmV0Y2g7MTA7MzMxNDY6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE1MDpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQzOlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNDc6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE0NTpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQ0OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNDI6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE0ODpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQ5OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNTE6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTswOw");
//		test04("cXVlcnlUaGVuRmV0Y2g7MTA7MzMxNDY6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE1MDpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQzOlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNDc6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE0NTpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQ0OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNDI6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE0ODpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQ5OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNTE6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTswOw");
//		test04("cXVlcnlUaGVuRmV0Y2g7MTA7MzMxNDY6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE1MDpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQzOlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNDc6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE0NTpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQ0OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNDI6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTszMzE0ODpUMmFhSzl0VVFLeURXT0JTNU1sMkdBOzMzMTQ5OlQyYWFLOXRVUUt5RFdPQlM1TWwyR0E7MzMxNTE6VDJhYUs5dFVRS3lEV09CUzVNbDJHQTswOw");
	}

}
