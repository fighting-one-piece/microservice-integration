package org.cisiondata.modules.boot;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.cisiondata.modules.qqrelation.service.IQQGraphService;
import org.cisiondata.modules.qqrelation.service.impl.QQGraphV1ServiceImpl;
import org.cisiondata.modules.qqrelation.service.impl.QQGraphV2ServiceImpl;
import org.cisiondata.modules.qqrelation.utils.ESClient;
import org.cisiondata.utils.date.DateFormatter;
import org.cisiondata.utils.http.HttpUtils;
import org.cisiondata.utils.json.GsonUtils;
import org.cisiondata.utils.titan.TitanUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;
import com.thinkaurelius.titan.core.TitanVertex;

public class QQRelationTest {
	
	public static void t1() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("insertTime", "2016-07-30 11:47:37");
		map.put("updateTime", "2016-07-30 10:12:20");
		map.put("sourceFile", "tb200912_4.txt");
		map.put("inputPerson", "zhangsan");
		map.put("qqNum", "129078294");
		map.put("password", "lisi04");
		map.put("gender", "1");
		map.put("age", "22");
		map.put("netDate", "2009");
		map.put("ip", "60.182.220.194");
		map.put("ipAddress", "浙江省金华市电信");
//		String nodeJSON = "{\"insertTime\":\"2016-07-30 11:47:37\",\"netDate\":\"2009\",\"ip\":\"60.182.220.194\",\"ipAddress\":\"地址:浙江省金华市电信\",\"updateTime\":\"2016-07-30 10:12:20\",\"qqNum\":\"1290782494\",\"sourceFile\":\"tb200912_4.txt\",\"qqPassword\":\"woaini520\"}";
		String nodeJSON = GsonUtils.fromMapToJson(map);
		String url = "http://localhost:8080/qq";
		Map<String, String> params = new HashMap<String, String>();
		params.put("nodeJSON", nodeJSON);
		String response = HttpUtils.sendPost(url, params);
		System.out.println(response);
	}
	
	public static void t2() throws ParseException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("insertTime", DateFormatter.TIME.get().parse("2016-07-30 11:47:37"));
		map.put("updateTime", DateFormatter.TIME.get().parse("2016-07-30 10:12:20"));
		map.put("sourceFile", "tb200912_4.txt");
		map.put("inputPerson", "zhangsan");
		map.put("qqNum", "9049965415");
		map.put("password", "lisi01");
		map.put("gender", "1");
		map.put("age", "22");
		map.put("netDate", "2009");
		map.put("ip", "60.182.220.194");
		map.put("ipAddress", "浙江省金华市电信");
		map.put("_id", "f14d3c672cf40a52ff1748bbea98a3e5");
		String nodeJSON = GsonUtils.fromMapToJson(map);
		IQQGraphService qqRelationService = new QQGraphV1ServiceImpl();
		qqRelationService.insertQQNode(nodeJSON);
	}
	
	public static void t3() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("qqNum", "1290782484");
		map.put("nickname", "李四03");
		map.put("gender", "1");
		map.put("age", "22");
		String nodeJSON = GsonUtils.fromMapToJson(map);
		Map<String, Object> newmap = GsonUtils.fromJsonToMap(nodeJSON);
		for (Map.Entry<String, Object> entry : newmap.entrySet()) {
			System.out.println(entry.getValue());
			System.out.println(entry.getValue().getClass());
		}
	}
	
	@Test
	public void t4_1() throws IOException {
		QQGraphV2ServiceImpl qqGraphService = new QQGraphV2ServiceImpl();
		List<String> lines0 = FileUtils.readLines(new File("F:\\result\\Desktop\\qq.txt"));
		qqGraphService.insertQQNodes(lines0);
		List<String> lines1 = FileUtils.readLines(new File("F:\\result\\Desktop\\qun.txt"));
		qqGraphService.insertQQQunNodes(lines1);
		TitanUtils.getInstance().closeGraph();
	}
	
	@Test
	public void t4_2() throws IOException {
		QQGraphV2ServiceImpl qqGraphService = new QQGraphV2ServiceImpl();
		List<String> lines2 = FileUtils.readLines(new File("F:\\result\\Desktop\\qqqun.txt"));
		qqGraphService.insertQQQunRelations(lines2);
		TitanUtils.getInstance().closeGraph();
	}
	
	public static void t5() throws InterruptedException, ExecutionException {
		Client client = ESClient.getInstance().getClient();
//		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
		String uniqueid = "f14d3c672cf40a52ff1748bbea98a3f3_380";
		SearchRequestBuilder srb = client.prepareSearch("titan").setTypes("qqnode");
		srb.setSearchType(SearchType.QUERY_AND_FETCH);
		srb.setQuery(QueryBuilders.termQuery("uniqueid", uniqueid));
		srb.setFrom(0).setSize(10);
		SearchResponse sr = srb.execute().get();
		long totalHits = sr.getHits().getTotalHits();
		System.out.println("totalHits: " + totalHits);
		if (totalHits > 0) {
			for (SearchHit hit : sr.getHits().getHits()) {
				System.out.println(hit.getId());
				System.out.println(hit.getSource());
			}
		}
		UpdateRequestBuilder urb = client.prepareUpdate("titan", "qqnode", "380");
		urb.setDoc("cnote", "{\"name\":\"zhangsan\"}");
		UpdateResponse ur = urb.execute().get();
		System.out.println("version: {}" + ur.getVersion());
	}
	
	@Test
	public void t6() {
		IQQGraphService qqRelationService = new QQGraphV2ServiceImpl();
		long start = System.currentTimeMillis();
		List<Map<String, Object>> qqNodes = qqRelationService.readQQNodeDataList("10024041");
		qqNodes.forEach(qq -> System.out.println(qq));
		System.out.println("spend time " + (System.currentTimeMillis() - start)/1000 + " s");
		TitanUtils.getInstance().closeGraph();
	}
	
	@Test
	public void t7() {
		IQQGraphService qqRelationService = new QQGraphV2ServiceImpl();
		long start = System.currentTimeMillis();
		List<Map<String, Object>> qunNodes = qqRelationService.readQunNodeDataList("11112201");
		System.out.println("spend time " + (System.currentTimeMillis() - start)/1000 + " s");
		qunNodes.forEach(qun -> System.out.println(qun));
		TitanUtils.getInstance().closeGraph();
	}
	
	@Test
	public void t8() {
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has("o46", "疯狂ING2");
		while (gt.hasNext()) {
        	Map<String, Object> result = new HashMap<String, Object>();
			Vertex vertex = gt.next();
			Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
			while (vertexProperties.hasNext()) {
				VertexProperty<Object> vp = vertexProperties.next();
				result.put(vp.key(), vp.value());
			}
			System.err.println(result);
        }
	}
	
	@Test
	public void t9() {
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		Iterator<Result<TitanVertex>> iterator = graph.indexQuery("qunvertex", "v.o34:(娱乐)").offset(0).limit(50).vertices().iterator();
		while (iterator.hasNext()) {
			Result<TitanVertex> result = iterator.next();
			TitanVertex vertex = result.getElement();
			Iterator<VertexProperty<Object>> edgeProperties = vertex.properties();
			Map<String, Object> finalResult = new HashMap<String, Object>();
			while (edgeProperties.hasNext()) {
				Property<Object> ep = edgeProperties.next();
				finalResult.put(ep.key(), ep.value());
			}
			System.err.println(finalResult);
		}
	}

}
