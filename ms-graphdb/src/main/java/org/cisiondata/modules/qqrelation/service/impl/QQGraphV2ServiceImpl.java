package org.cisiondata.modules.qqrelation.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.cisiondata.modules.qqrelation.service.IQQGraphService;
import org.cisiondata.utils.exception.BusinessException;
import org.cisiondata.utils.json.GsonUtils;
import org.cisiondata.utils.titan.TitanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thinkaurelius.titan.core.TitanEdge;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;

@Service("qqGraphV2Service")
public class QQGraphV2ServiceImpl implements IQQGraphService {
	
	private Logger LOG = LoggerFactory.getLogger(QQGraphV2ServiceImpl.class);
	
	/**
	 * i4 QQ号 i6 邮箱 i60 邮箱密码 i61 QQ密码 c1 年龄 c3 性别
	 * i50 QUN号 o46 群名称 o17 群人数 o34 群通知 o35 群类型 d21 创建时间
	 */
	private static final String NODE_QQ = "qq";
	private static final String NODE_QUN = "qun";
	private static final String _ID = "_id";
	private static final String QQ_ID = "_id1";
	private static final String QUN_ID = "_id2";
	private static final String QQ_NUM = "i4";
	private static final String QQ_AGE = "c1";
	private static final String QQ_AUTH = "c2";
	private static final String QQ_GENDER = "c3";
	private static final String QQ_NICKNAME = "o23";
	private static final String QQ_PASS = "i61";
	private static final String QQ_EMAIL = "i6";
	private static final String QQ_EPASS = "i60";
	private static final String QQ_CNOTE = "c139";
	private static final String QUN_NUM = "i50";
	private static final String QUN_NAME = "o46";
	private static final String QUN_PS = "o17";
	private static final String QUN_DESC = "o34";
	private static final String QUN_TYPE = "o35";
	private static final String QUN_CT = "d21";
	
	private static final String NICKNAME_QUERY = "e.o23:(%s)";
	
	private static Set<String> QQ_KEYS = new HashSet<String>();
	private static Set<String> QUN_KEYS = new HashSet<String>();
	
	static {
		QQ_KEYS.add(_ID);
		QQ_KEYS.add(QQ_ID);
		QQ_KEYS.add(QQ_NUM);
		QQ_KEYS.add(QQ_AGE);
		QQ_KEYS.add(QQ_GENDER);
		QQ_KEYS.add(QQ_PASS);
		QQ_KEYS.add(QQ_EMAIL);
		QQ_KEYS.add(QQ_EPASS);
		QQ_KEYS.add(QQ_CNOTE);
		QUN_KEYS.add(_ID);
		QUN_KEYS.add(QUN_ID);
		QUN_KEYS.add(QUN_NUM);
		QUN_KEYS.add(QUN_NAME);
		QUN_KEYS.add(QUN_PS);
		QUN_KEYS.add(QUN_DESC);
		QUN_KEYS.add(QUN_TYPE);
		QUN_KEYS.add(QUN_CT);
	}
	
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
					if (vertexPropertiesKeys.contains(QQ_ID)) {
						VertexProperty<String> idVP = vertex.property(QQ_ID);
						String uniqueid = null != idVP.value() ? idVP.value() : "";
						if (!uniqueid.equals(node.get(_ID))) {
							LOG.error("qqnum {} has existed!", node.get(QQ_NUM));
						}
					}
				} else {
					Vertex vertex = graph.addVertex(NODE_QQ);
					for (Map.Entry<String, Object> entry : node.entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();
						if (!QQ_KEYS.contains(key) || null == value) continue;
						if (_ID.equals(key)) {
							key = QQ_ID;
						} else if (QQ_AGE.equals(key)) {
							value = Integer.parseInt(String.valueOf(value));
						}
						vertex.property(key, value);
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
					if (vertexPropertiesKeys.contains(QUN_ID)) {
						VertexProperty<String> idVP = vertex.property(QUN_ID);
						String uniqueid = null != idVP.value() ? idVP.value() : "";
						if (!uniqueid.equals(node.get(_ID))) {
							LOG.error("qunnum {} has existed!", node.get(QUN_NUM));
						}
					}
				} else {
					Vertex vertex = graph.addVertex(NODE_QUN);
					for (Map.Entry<String, Object> entry : node.entrySet()) {
						String key = entry.getKey();
						Object value = entry.getValue();
						if (!QUN_KEYS.contains(key) || null == value) continue;
						if (_ID.equals(key)) {
							key = QUN_ID;
						}else if (QUN_PS.equals(key)) {
							value = Integer.parseInt(String.valueOf(value));
						}
						vertex.property(key, value);
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
				/**
				Edge includedEdge = qqVertex.addEdge("included", qunVertex);
				includingEdge.property(QQ_NUM, qqNum);
				includedEdge.property(QUN_NUM, qunNum);
				**/
				Object genderObj = node.get(QQ_GENDER);
				if (null != genderObj) {
					includingEdge.property(QQ_GENDER, genderObj);
					/**
					includedEdge.property(QQ_GENDER, genderObj);
					**/
				}
				Object ageObj = node.get(QQ_AGE);
				if (null != ageObj) {
					includingEdge.property(QQ_AGE, ageObj);
					/**
					includedEdge.property(QQ_AGE, ageObj);
					**/
				}
				Object authObj = node.get(QQ_AUTH);
				if (null != authObj) {
					includingEdge.property(QQ_AUTH, authObj);
					/**
					includedEdge.property(QQ_AUTH, authObj);
					**/
				}
				Object nicknameObj = node.get(QQ_NICKNAME);
				if (null != nicknameObj) {
					includingEdge.property(QQ_NICKNAME, nicknameObj);
					/**
					includedEdge.property(QQ_NICKNAME, nicknameObj);
					**/
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
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has(QQ_NUM, qqNum);
		while (gt.hasNext()) {
        	Map<String, Object> result = new HashMap<String, Object>();
			Vertex vertex = gt.next();
			Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
			while (vertexProperties.hasNext()) {
				VertexProperty<Object> vp = vertexProperties.next();
				result.put(vp.key(), vp.value());
			}
			List<Map<String, Object>> qunResultList = new ArrayList<Map<String, Object>>();
			Iterator<Edge> edgeIterator = vertex.edges(Direction.IN);
			while (edgeIterator.hasNext()) {
				Map<String, Object> qunResult = new HashMap<String, Object>();
				Edge edge = edgeIterator.next();
				Iterator<Property<Object>> edgeProperties = edge.properties();
				while (edgeProperties.hasNext()) {
					Property<Object> ep = edgeProperties.next();
					qunResult.put(ep.key(), ep.value());
				}
				Iterator<VertexProperty<Object>> outVertexProperties = edge.outVertex().properties();
				while (outVertexProperties.hasNext()) {
					VertexProperty<Object> ivp = outVertexProperties.next();
					qunResult.put(ivp.key(), ivp.value());
				}
				qunResultList.add(qunResult);
			}
			result.put("quns", qunResultList);
			resultList.add(result);
        }
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> readQQNodeDataListByNickname(String nickname) throws BusinessException {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		Iterator<Result<TitanEdge>> iterator = graph.indexQuery("relationedge", String.format(
			NICKNAME_QUERY, nickname)).offset(0).limit(50).edges().iterator();
		while (iterator.hasNext()) {
			Result<TitanEdge> result = iterator.next();
			TitanEdge edge = result.getElement();
			Iterator<Property<Object>> edgeProperties = edge.properties();
			Map<String, Object> finalResult = new HashMap<String, Object>();
			while (edgeProperties.hasNext()) {
				Property<Object> ep = edgeProperties.next();
				finalResult.put(ep.key(), ep.value());
			}
			Iterator<Vertex> vertices = edge.bothVertices();
			while (vertices.hasNext()) {
				Iterator<VertexProperty<Object>> inVertexProperties = vertices.next().properties();
				while (inVertexProperties.hasNext()) {
					VertexProperty<Object> ivp = inVertexProperties.next();
					finalResult.put(ivp.key(), ivp.value());
				}
			}
			finalResult.put("score", result.getScore());
			resultList.add(finalResult);
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
				Iterator<VertexProperty<Object>> inVertexProperties = edge.inVertex().properties();
				while (inVertexProperties.hasNext()) {
					VertexProperty<Object> ivp = inVertexProperties.next();
					qqResult.put(ivp.key(), ivp.value());
				}
				qqResultList.add(qqResult);
			}
			result.put("qqs:", qqResultList);
			resultList.add(result);
        }
		return resultList;
	}
	
}
