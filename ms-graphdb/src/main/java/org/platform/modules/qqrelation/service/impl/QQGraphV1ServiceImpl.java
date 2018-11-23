package org.platform.modules.qqrelation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.platform.modules.qqrelation.service.IQQGraphService;
import org.platform.modules.qqrelation.utils.ESClient;
import org.platform.utils.date.DateFormatter;
import org.platform.utils.exception.BusinessException;
import org.platform.utils.json.GsonUtils;
import org.platform.utils.titan.TitanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thinkaurelius.titan.core.TitanGraph;

@Service("qqGraphV1Service")
public class QQGraphV1ServiceImpl implements IQQGraphService {
	
	private Logger LOG = LoggerFactory.getLogger(QQGraphV1ServiceImpl.class);
	
	private static final String NODE_QQ = "qq";
	private static final String NODE_QUN = "qun";
	private static final String ID = "_id";
	private static final String QQ_NUM = "i4";
	private static final String QQ_AGE = "o23";
	private static final String QQ_AUTH = "c2";
	private static final String QQ_GENDER = "c129";
	private static final String QQ_NICKNAME = "o25";
	private static final String QUN_NUM = "i50";
	private static final String UNIQUE_ID = "uid";
	private static final String CNOTE = "o7";
	private static final String UPDATE_TIME = "c138";
	
	@Override
	public void insertQQNode(String nodeJSON) throws BusinessException {
		List<String> nodes = new ArrayList<String>();
		nodes.add(nodeJSON);
		insertQQNodes(nodes);
	}
	
	@Override
	public void insertQQNodes(List<String> nodes) throws BusinessException {
		if (null == nodes || nodes.size() == 0) return;
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		try {
			for (int i = 0, len = nodes.size(); i < len; i++) {
				Map<String, Object> node = GsonUtils.fromJsonToMap(nodes.get(i));
				GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has(QQ_NUM, node.get(QQ_NUM));
				if (gt.hasNext()) {
					Vertex vertex = gt.next();
					Set<String> vertexPropertiesKeys = vertex.keys();
					if (vertexPropertiesKeys.contains(UNIQUE_ID)) {
						VertexProperty<String> idVP = vertex.property(UNIQUE_ID);
						String uniqueid = null != idVP.value() ? idVP.value() : "";
						if (!uniqueid.equals(node.get(ID))) {
							updateESNodeCnoteData("titan", "qqnode", uniqueid, nodes.get(i));
						}
					}
				} else {
					Vertex vertex = graph.addVertex(NODE_QQ);
					for (Map.Entry<String, Object> entry : node.entrySet()) {
						String key = entry.getKey();
						if (ID.equals(key)) vertex.property(UNIQUE_ID, entry.getValue());
						if ("insertTime".equals(key) || UPDATE_TIME.equals(key)) {
							vertex.property(key, DateFormatter.TIME.get().parse(String.valueOf(entry.getValue())));
						} else {
							vertex.property(key, entry.getValue());
						}
					}
				}
			}
			graph.tx().commit();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void insertQQQunNode(String nodeJSON) throws BusinessException {
		List<String> nodes = new ArrayList<String>();
		nodes.add(nodeJSON);
		insertQQQunNodes(nodes);
	}
	
	@Override
	public void insertQQQunNodes(List<String> nodes) throws BusinessException {
		if (null == nodes || nodes.size() == 0) return;
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		try {
			for (int i = 0, len = nodes.size(); i < len; i++) {
				Map<String, Object> node = GsonUtils.fromJsonToMap(nodes.get(i));
				GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has(QUN_NUM, node.get(QUN_NUM));
				if (gt.hasNext()) {
					Vertex vertex = gt.next();
					Set<String> vertexPropertiesKeys = vertex.keys();
					if (vertexPropertiesKeys.contains(UNIQUE_ID)) {
						VertexProperty<String> idVP = vertex.property(UNIQUE_ID);
						String uniqueid = null != idVP.value() ? idVP.value() : "";
						if (!uniqueid.equals(node.get(ID))) {
							updateESNodeCnoteData("titan", "qunnode", uniqueid, nodes.get(i));
						}
					}
				} else {
					Vertex vertex = graph.addVertex(NODE_QUN);
					for (Map.Entry<String, Object> entry : node.entrySet()) {
						String key = entry.getKey();
						if (ID.equals(key)) vertex.property(UNIQUE_ID, entry.getValue());
						if ("insertTime".equals(key) || UPDATE_TIME.equals(key)) {
							vertex.property(key, DateFormatter.TIME.get().parse(String.valueOf(entry.getValue())));
						} else {
							vertex.property(key, entry.getValue());
						}
					}
				}
			}
			graph.tx().commit();
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void insertQQQunRelation(String nodeJSON) throws BusinessException {
		List<String> nodes = new ArrayList<String>();
		nodes.add(nodeJSON);
		insertQQQunRelations(nodes);
	}
	
	@Override
	public void insertQQQunRelations(List<String> nodes) throws BusinessException {
		if (null == nodes || nodes.size() == 0) return;
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		try {
			for (int i = 0, len = nodes.size(); i < len; i++) {
				Map<String, Object> node = GsonUtils.fromJsonToMap(nodes.get(i));
				String qqNum = String.valueOf(node.get(QQ_NUM));
				String qunNum = String.valueOf(node.get(QUN_NUM));
				GraphTraversal<Vertex, Vertex> gt1 = graph.traversal().V().has(QQ_NUM, qqNum);
				Vertex qqVertex = null;
				if (gt1.hasNext()) {
					qqVertex = gt1.next();
				} else {
					qqVertex = graph.addVertex(NODE_QQ);
					qqVertex.property(QQ_NUM, qqNum);
				}
				GraphTraversal<Vertex, Vertex> gt2 = graph.traversal().V().has(QUN_NUM, qunNum);
				Vertex qunVertex = null;
				if (gt2.hasNext()) {
					qunVertex = gt2.next();
				} else {
					qunVertex = graph.addVertex(NODE_QUN);
					qunVertex.property(QUN_NUM, qunNum);
				}
				Edge includingEdge = qunVertex.addEdge("including", qqVertex);
				Edge includedEdge = qqVertex.addEdge("included", qunVertex);
				includingEdge.properties(QQ_NUM, qqNum);
				includedEdge.properties(QUN_NUM, qunNum);
				Object genderObj = node.get(QQ_GENDER);
				if (null != genderObj) {
					includingEdge.property(QQ_GENDER, genderObj);
					includedEdge.property(QQ_GENDER, genderObj);
				}
				Object ageObj = node.get(QQ_AGE);
				if (null != ageObj) {
					includingEdge.property(QQ_AGE, ageObj);
					includedEdge.property(QQ_AGE, ageObj);
				}
				Object authObj = node.get(QQ_AUTH);
				if (null != authObj) {
					includingEdge.property(QQ_AUTH, authObj);
					includedEdge.property(QQ_AUTH, authObj);
				}
				Object nicknameObj = node.get(QQ_NICKNAME);
				if (null != nicknameObj) {
					includingEdge.property(QQ_NICKNAME, nicknameObj);
					includedEdge.property(QQ_NICKNAME, nicknameObj);
				}
			}
			graph.tx().commit();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	@Override
	public List<Map<String, Object>> readQQNodeDataList(String qqNum) throws BusinessException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		long start = System.currentTimeMillis();
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		System.out.println("1:" + (System.currentTimeMillis()-start)/1000);
		GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has(QQ_NUM, qqNum);
		System.out.println("2:" + (System.currentTimeMillis()-start)/1000);
		while (gt.hasNext()) {
        	Map<String, Object> result = new HashMap<String, Object>();
			Vertex vertex = gt.next();
			System.out.println("3:" + (System.currentTimeMillis()-start)/1000);
			Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
			while (vertexProperties.hasNext()) {
				VertexProperty<Object> vp = vertexProperties.next();
				result.put(vp.key(), vp.value());
			}
			Iterator<Edge> edgeIterator = vertex.edges(Direction.OUT);
			System.out.println("4:" + (System.currentTimeMillis()-start)/1000);
			List<Map<String, Object>> qunResultList = new ArrayList<Map<String, Object>>();
			while (edgeIterator.hasNext()) {
				System.out.println("5:" + (System.currentTimeMillis()-start)/1000);
				Map<String, Object> qunResult = new HashMap<String, Object>();
				Edge edge = edgeIterator.next();
				Iterator<Property<Object>> edgeProperties = edge.properties();
				while (edgeProperties.hasNext()) {
					Property<Object> ep = edgeProperties.next();
					qunResult.put(ep.key(), ep.value());
				}
				qunResultList.add(qunResult);
			}
			resultList.add(result);
        }
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> readQQNodeDataListByNickname(String nickname) throws BusinessException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		GraphTraversal<Edge, Edge> gt = graph.traversal().E().has(QQ_NICKNAME, nickname);
		while (gt.hasNext()) {
			Map<String, Object> result = new HashMap<String, Object>();
			Edge edge = gt.next();
			Iterator<Property<Object>> edgeProperties = edge.properties();
			while (edgeProperties.hasNext()) {
				Property<Object> ep = edgeProperties.next();
				result.put(ep.key(), ep.value());
			}
			resultList.add(result);
		}
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> readQunNodeDataList(String qunNum) throws BusinessException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has(QUN_NUM, qunNum);
        while (gt.hasNext()) {
        	Map<String, Object> result = new HashMap<String, Object>();
			Vertex vertex = gt.next();
			Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
			while (vertexProperties.hasNext()) {
				VertexProperty<Object> vp = vertexProperties.next();
				result.put(vp.key(), vp.value());
			}
			Iterator<Edge> edgeIterator = vertex.edges(Direction.OUT);
			List<Map<String, Object>> qqResultList = new ArrayList<Map<String, Object>>();
			while (edgeIterator.hasNext()) {
				Map<String, Object> qqResult = new HashMap<String, Object>();
				Edge edge = edgeIterator.next();
				Iterator<Property<Object>> edgeProperties = edge.properties();
				while (edgeProperties.hasNext()) {
					Property<Object> ep = edgeProperties.next();
					qqResult.put(ep.key(), ep.value());
				}
				qqResultList.add(qqResult);
			}
			resultList.add(result);
        }
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> readDataList(String keyword) throws BusinessException {
		return null;
	}
	
	private synchronized void updateESNodeCnoteData(String index, String type, String uniqueid, String cnote) {
		try {
			Client client = ESClient.getInstance().getClient();
			SearchRequestBuilder srb = client.prepareSearch("titan").setTypes("qqnode");
			srb.setSearchType(SearchType.QUERY_AND_FETCH);
			srb.setQuery(QueryBuilders.termQuery(UNIQUE_ID, uniqueid));
			srb.setFrom(0).setSize(10);
			SearchResponse sr = srb.execute().get();
			long totalHits = sr.getHits().getTotalHits();
			if (totalHits > 0) {
				SearchHit hit = sr.getHits().getHits()[0];
				UpdateRequestBuilder urb = client.prepareUpdate("titan", "qqnode", hit.getId());
				Object cnoteObj = hit.getSource().get(CNOTE);
				urb.setDoc(CNOTE, null != cnoteObj ? cnoteObj + cnote : cnote);
				UpdateResponse ur = urb.execute().get();
				LOG.info("version: {}", ur.getVersion());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
}
