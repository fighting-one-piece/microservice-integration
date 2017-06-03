package org.cisiondata.modules.qqrelation.service;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.cisiondata.utils.titan.TitanUtils;

import com.thinkaurelius.titan.core.TitanGraph;

public class QQGraphUtils {
	
	public void buildOriginalSchema(TitanGraph graph) {
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "uniqueid", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "qqNum", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "password", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "ip", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "ipAddress", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "netDate", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "netTime", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "age", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "gender", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "state", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "qqCoin", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "qqPoint", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "province", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "security", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "cnote", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "updateTime", Date.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "sourceFile", String.class);
		/**
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "insertTime", Date.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "inputPerson", String.class);
		*/
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "qunNum", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "qunName", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "qunNotice", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "qunPersonNum", Integer.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "qunCreateDate", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "qunLevel", Integer.class);
	    
		TitanUtils.getInstance().buildEdgeLabel("included");
		TitanUtils.getInstance().buildEdgeLabel("including");
	}
	
	public void buildSchema(TitanGraph graph) {
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "uid", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "i4", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "i9", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c1", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "a26", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c2", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c3", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "o23", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c129", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c4", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c5", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c6", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "a5", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c134", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "o7", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c138", Date.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "c136", String.class);
		/**
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "insertTime", Date.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqnode", "inputPerson", String.class);
		*/
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "i50", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "o24", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "c4", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "c1", Integer.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "c2", String.class);
		TitanUtils.getInstance().buildMixedIndexForVertexProperty("qqqunnode", "c3", Integer.class);
	    
		TitanUtils.getInstance().buildEdgeLabel("included");
		TitanUtils.getInstance().buildEdgeLabel("including");
	}
	
	public void queryNode(TitanGraph graph) {
	    try {
	        GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has("qqNum", "422345678");
	        while (gt.hasNext()) {
				Vertex vertex = gt.next();
				System.out.println("vertex label: " + vertex.label());
				Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
				while (vertexProperties.hasNext()) {
					VertexProperty<Object> vp = vertexProperties.next();
					System.out.println(vp.key() + ":" + vp.value());
				}
				System.out.println("$$$$$$");
				Iterator<Edge> edgeIterator = vertex.edges(Direction.BOTH);
				while (edgeIterator.hasNext()) {
					Edge edge = edgeIterator.next();
					System.out.println(edge.label());
				}
				System.out.println("######");
	        }
	        System.out.println("qqnum query finish!");
	        List<Vertex> verties = graph.traversal().V().toList();
	        for (Vertex vertex : verties) {
	        	System.out.println("vertex label: " + vertex.label());
				Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
				while (vertexProperties.hasNext()) {
					VertexProperty<Object> vp = vertexProperties.next();
					System.out.println(vp.key() + ":" + vp.value());
				}
				System.out.println("All$$$$$$All");
	        }
	        Vertex vertex1 = graph.traversal().V().has("qunNum", "345678").next();
	        System.out.println("qun name: " +vertex1.value("name").toString());
	        System.out.println("qun date: " + vertex1.value("createDate").toString());
	    } catch (NoSuchElementException e) {
	        e.printStackTrace();
	    } finally {
	        graph.tx().close();
	    }
	}
	
	public void queryOneNode(TitanGraph graph) {
		try {
	        GraphTraversal<Vertex, Vertex> gt = graph.traversal().V().has("qqNum", "843766437");
	        while (gt.hasNext()) {
				Vertex vertex = gt.next();
				System.out.println("vertex label: " + vertex.label());
				Iterator<VertexProperty<Object>> vertexProperties = vertex.properties();
				while (vertexProperties.hasNext()) {
					VertexProperty<Object> vp = vertexProperties.next();
					System.out.println(vp.key() + ":" + vp.value());
				}
				System.out.println("$$$$$$");
				Iterator<Edge> edgeIterator = vertex.edges(Direction.BOTH);
				while (edgeIterator.hasNext()) {
					Edge edge = edgeIterator.next();
					System.out.println("edge label: " + edge.label());
					Iterator<Property<Object>> edgeProperties = edge.properties();
					while (edgeProperties.hasNext()) {
						Property<Object> ep = edgeProperties.next();
						System.out.println("ep: " + ep.key() + " = " + ep.value());
					}
				}
				System.out.println("######");
	        }
		} catch (NoSuchElementException e) {
	        e.printStackTrace();
	    } finally {
	        graph.tx().close();
	    }
	}
	
	public static void main(String[] args) throws ParseException {
		TitanGraph graph = TitanUtils.getInstance().getGraph();
		QQGraphUtils qqRelationGraph = new QQGraphUtils();
		qqRelationGraph.buildSchema(graph);
//		qqRelationGraph.loadNode(graph);
//		qqRelationGraph.queryNode(graph
//		qqRelationGraph.queryOneNode(graph);
		graph.close();
	}
	
}
