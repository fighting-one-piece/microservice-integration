package org.cisiondata.modules;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cisiondata.modules.elastic.entity.BoolCondition;
import org.cisiondata.modules.elastic.entity.Condition;
import org.cisiondata.modules.elastic.entity.TermCondition;
import org.cisiondata.modules.elastic.utils.ElasticClient;
import org.cisiondata.modules.elastic.utils.ElasticUtils;
import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.json.GsonUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import com.google.gson.Gson;

public class WebTest {
	
	@Test
	public void t1() throws UnsupportedEncodingException {
		String url = "http://host-125:10020/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "financial");
		params.put("t", "logistics");
		params.put("hl", 1);
		params.put("q", URLEncoder.encode("13612345678", "utf-8"));
		params.put("pn", 1);
		params.put("rn", 2);
		String response1 = HttpUtils.sendGet(url, params);
		System.err.println(response1);
		Map<String, Object> subparams = new HashMap<String, Object>();
		subparams.put("mobilePhone", "13512345678");
		subparams.put("linkMobilePhone", "13512345678");
		params.put("q", URLEncoder.encode(GsonUtils.fromMapToJson(subparams), "utf-8"));
		String response2 = HttpUtils.sendGet(url, params);
		System.err.println(response2);
	}
	
	@Test
	public void t2() throws UnsupportedEncodingException {
		BoolCondition boolCondition = new BoolCondition();
		boolCondition.must(new TermCondition("mobilePhone", "13412345678"));
		boolCondition.must(new TermCondition("age", 21));
		BoolCondition subBoolCondition = new BoolCondition();
		subBoolCondition.should(new TermCondition("name", "zhangsan"));
		subBoolCondition.should(new TermCondition("idcard", "123"));
		boolCondition.must(subBoolCondition);
		String json = new Gson().toJson(boolCondition);
		System.err.println("json: " + json);
		
		BoolCondition boolCondition1 = GsonUtils.builder().fromJson(json, BoolCondition.class);
		System.err.println("c json: " + new Gson().toJson(boolCondition1));
		List<Condition> conditions1 = boolCondition1.getMustClauses();
		for (int i = 0, len = conditions1.size(); i < len; i++) {
			System.err.println(conditions1.get(i).getClass());
		}
	}

	@Test
	public void t3() throws UnsupportedEncodingException {
		String url = "http://localhost:10020/elastic/search";
//		String url = "http://host-125:20030/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "qq");
		params.put("t", "qqqunrelation");
		params.put("hl", 1);
		params.put("pn", 1);
		params.put("rn", 2);
		TermCondition termCondition = new TermCondition("nick", "宁馨儿");
		params.put("q", URLEncoder.encode(new Gson().toJson(termCondition), "utf-8"));
		String response1 = HttpUtils.sendGet(url, params);
		System.err.println(response1);
	}
	
	@Test
	public void t4() throws UnsupportedEncodingException {
//		String url = "http://localhost:10020/elastic/search";
		String url = "http://host-125:20030/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "qq");
		params.put("t", "qqqunrelation");
		params.put("hl", 1);
		params.put("pn", 1);
		params.put("rn", 2);
		BoolCondition boolCondition = new BoolCondition();
		boolCondition.must(new TermCondition("nick", "乖宝"));
//		boolCondition.must(new TermCondition("qqNum", "977679456"));
//		boolCondition.must(new TermCondition("linkMobilePhone", "13512345678"));
		params.put("q", URLEncoder.encode(new Gson().toJson(boolCondition), "utf-8"));
		String response1 = HttpUtils.sendGet(url, params);
		System.err.println(response1);
	}
	
	@Test
	public void t5() throws UnsupportedEncodingException {
		String url = "http://host-125:10020/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "financial");
		params.put("t", "logistics");
		params.put("hl", 1);
		params.put("pn", 1);
		params.put("rn", 10);
		BoolCondition boolCondition = new BoolCondition();
		boolCondition.should(new TermCondition("mobilePhone", "13459701376"));
		boolCondition.should(new TermCondition("linkMobilePhone", "18718991677"));
		params.put("q", URLEncoder.encode(new Gson().toJson(boolCondition), "utf-8"));
		String response1 = HttpUtils.sendGet(url, params);
		System.err.println(response1);
	}
	
	@Test
	public void t6() throws UnsupportedEncodingException {
		String url = "http://host-125:10020/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "financial");
		params.put("t", "logistics");
		params.put("hl", 1);
		params.put("pn", 1);
		params.put("rn", 10);
		BoolCondition boolCondition = new BoolCondition();
		boolCondition.must(new TermCondition("mobilePhone", "13459701376"));
		BoolCondition subBoolCondition = new BoolCondition();
		subBoolCondition.should(new TermCondition("linkName", "江淑英"));
		boolCondition.must(subBoolCondition);
		params.put("q", URLEncoder.encode(new Gson().toJson(boolCondition), "utf-8"));
		String response = HttpUtils.sendGet(url, params);
		System.err.println(response);
	}
	
	@Test
	public void t7() throws UnsupportedEncodingException {
		String url = "http://host-125:10020/elastic/search";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("i", "financial");
		params.put("t", "logistics");
		params.put("hl", 1);
		params.put("pn", 1);
		params.put("rn", 10);
		BoolCondition boolCondition = new BoolCondition();
		boolCondition.should(new TermCondition("_id", "cb2d831514fd2191e1675f6ef5b74d39"));
		boolCondition.should(new TermCondition("_id", "0c15b51fa977f72356ec82f9a770716b"));
		params.put("q", URLEncoder.encode(new Gson().toJson(boolCondition), "utf-8"));
		String response = HttpUtils.sendGet(url, params);
		System.err.println(response);
	}
	
	@Test
	public void t8() throws UnsupportedEncodingException {
		Client client = ElasticClient.getInstance().getClient();
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		QueryBuilder queryBuilder1 = QueryBuilders.termQuery("_id", "a7984439394a9e2d75fd0a80518b934e");
		QueryBuilder queryBuilder2 = QueryBuilders.termQuery("_id", "ceae8214813ff2dee883fd8cafe63aaa");
		boolQueryBuilder.should(queryBuilder1);
		boolQueryBuilder.should(queryBuilder2);
		SearchResponse response = client.prepareSearch("financial").setTypes("logistics")
				.setQuery(boolQueryBuilder).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setSize(1000).setExplain(false).execute().actionGet();
		System.err.println("total hits: " + response.getHits().getTotalHits());
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0, len = hits.length; i < len; i++) {
			System.err.println(hits[i].getSourceAsMap());
		}
	}
	
	@Test
	public void t9() throws UnsupportedEncodingException {
		Client client = ElasticClient.getInstance().getClient();
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		QueryBuilder queryBuilder1 = QueryBuilders.termQuery("_id", "c8550d0414e09c65e50d7cac129b1adf");
		QueryBuilder queryBuilder2 = QueryBuilders.termQuery("_id", "408a7f082f4de2a202056e8aaefac6b0");
		boolQueryBuilder.should(queryBuilder1);
		boolQueryBuilder.should(queryBuilder2);
		SearchResponse response = client.prepareSearch("car-v1").setTypes("car")
				.setQuery(boolQueryBuilder).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setSize(1000).setExplain(false).execute().actionGet();
		System.err.println("total hits: " + response.getHits().getTotalHits());
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0, len = hits.length; i < len; i++) {
			for (Map.Entry<String, Object> entry : hits[i].getSourceAsMap().entrySet()) {
				System.err.println(entry.getKey() + ":" + entry.getValue() + ":" + entry.getValue().getClass());
			}
		}
	}
	
	@Test
	public void t10() throws UnsupportedEncodingException {
		Client client = ElasticClient.getInstance().getClient();
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		QueryBuilder queryBuilder1 = QueryBuilders.termQuery("_id", "c8550d0414e09c65e50d7cac129b1adf");
		boolQueryBuilder.should(queryBuilder1);
		SearchResponse response = client.prepareSearch("cybercafe-v1").setTypes("cybercafe")
				.setQuery(boolQueryBuilder).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setSize(1000).setExplain(false).execute().actionGet();
		System.err.println("total hits: " + response.getHits().getTotalHits());
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0, len = hits.length; i < len; i++) {
			for (Map.Entry<String, Object> entry : hits[i].getSourceAsMap().entrySet()) {
				System.err.println(entry.getKey() + ":" + entry.getValue() + ":" + entry.getValue().getClass());
			}
		}
	}
	
	@Test
	public void t11() throws Exception {
		String[] words1 = ElasticUtils.convertSynonyms("李其恩");
		for (String word : words1) {
			System.err.println(word);
		}
		String[] words2 = ElasticUtils.convertSynonyms("成都");
		for (String word : words2) {
			System.err.println(word);
		}
	}
	
	@Test
	public void t12() {
		String url = "http://localhost:10020/cache/ks/delete";
		Map<String, String> params = new HashMap<String, String>();
		params.put("pattern", "resource:c:id:217");
		String response = HttpUtils.sendPost(url, params);
		System.err.println(response);
	}
	
	
	public static void main(String[] args) {
	}
	
}
