package org.platform.utils.titan;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanFactory.Builder;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.core.schema.Parameter;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;

public class TitanUtils {
	
	private Logger LOG = LoggerFactory.getLogger(TitanUtils.class);

	private TitanGraph graph = null;
	
	private TitanUtils() {
		loadTitanGraph();
	}
	
	private void loadTitanGraph() {
		Builder builder = TitanFactory.build();
		builder.set("storage.backend", "hbase");
        builder.set("storage.hostname", "host-124");
        builder.set("storage.port", "2181");
        //设置没有作用
        builder.set("storage.tablename", "qqgraph");
        builder.set("atlas.graph.storage.hbase.table", "qqgraph");
        builder.set("index.search.backend", "elasticsearch");
        builder.set("index.search.hostname", "host-124");
        builder.set("index.search.port", "9300");
        //设置ES Index名称
        builder.set("index.search.index-name", "qqgraph");
        /**
        builder.set("index.search.directory", "/tmp/titan" + File.separator + "es");  
        */
        builder.set("index.search.elasticsearch.interface", "TRANSPORT_CLIENT");
        builder.set("index.search.elasticsearch.cluster-name", "platform-graphdb");
        builder.set("index.search.elasticsearch.local-mode", false);  
        builder.set("index.search.elasticsearch.client-only", true);
        builder.set("cache.db-cache", true);
        builder.set("cache.db-cache-clean-wait", 20);
        builder.set("cache.db-cache-time", 180000);
        builder.set("cache.db-cache-size", 0.25);
        builder.set("query.fast-property", true);
        builder.set("graph.set-vertex-id", false);
        this.graph = builder.open();
        /**
        this.graph.configuration().setProperty(GraphDatabaseConfiguration.ALLOW_SETTING_VERTEX_ID.toString(), true);
		**/
	}
	
	private static class TitanUtilsHolder {
		public static TitanUtils INSTANCE = new TitanUtils();
	}
	
	public static TitanUtils getInstance() {
		return TitanUtilsHolder.INSTANCE;
	}
	
	public TitanGraph getGraph() {
		if (null == graph) loadTitanGraph();
		return graph;
	}
	
	public void closeGraph() {
		if (null != graph) graph.close();
	}
	
	public TitanManagement openManagement() {
		return graph.openManagement();
	}
	
	public void commit(TitanManagement mgmt) {
		mgmt.commit();
	}
	
	public void rollback(TitanManagement mgmt) {
		mgmt.rollback();
	}
	
	/**
	 * 构建PropertyKey
	 * @param propertyKeyName
	 * @param propertyKeyType
	 * @return
	 */
	public PropertyKey buildPropertyKey(String propertyKeyName, Class<?> propertyKeyType) {
		TitanManagement mgmt = openManagement();
		PropertyKey propertyKey = null;
		try {
			propertyKey = buildPropertyKey(openManagement(), propertyKeyName, propertyKeyType);
			mgmt.commit();
	    } catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	        mgmt.rollback();
	    } 
		return propertyKey;
	}
	
	/**
	 * 构建PropertyKey
	 * @param mgmt
	 * @param propertyKeyName
	 * @param propertyKeyType
	 * @return
	 */
	public PropertyKey buildPropertyKey(TitanManagement mgmt, String propertyKeyName, Class<?> propertyKeyType) {
		return mgmt.containsPropertyKey(propertyKeyName) ? mgmt.getPropertyKey(propertyKeyName) :
			mgmt.makePropertyKey(propertyKeyName).dataType(propertyKeyType).make();
	}
	
	/**
	 * 构建PropertyKey并加入MixedIndex
	 * @param indexName
	 * @param elementType Vertex or Edge
	 * @param propertyKeyName
	 * @param propertyKeyType
	 * @param mapping  STRING -> not_analyzed TEXT -> tokenized TEXTSTRING -> both
	 */
	public void buildPropertyKeyWithMixedIndex(String indexName, Class<? extends Element> elementType, 
			String propertyKeyName, Class<?> propertyKeyType, Mapping mapping) {
		TitanManagement mgmt = openManagement();
		try {
	        PropertyKey propertyKey = buildPropertyKey(mgmt, propertyKeyName, propertyKeyType);
	        if (mgmt.containsGraphIndex(indexName)) {
	        	mgmt.addIndexKey(mgmt.getGraphIndex(indexName), propertyKey, 
	        		Parameter.of("mapping", mapping));
	        } else {
	        	mgmt.buildIndex(indexName, elementType).addKey(propertyKey, 
	        		Parameter.of("mapping", mapping)).buildMixedIndex("search");
	        }
	        mgmt.commit();
	    } catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	        mgmt.rollback();
	    } 
	}
	
	/**
	 * 构建EdgeLabel
	 * @param edgeLabel
	 */
	public void buildEdgeLabel(String edgeLabel) {
		TitanManagement mgmt = openManagement();
		try {
			if (!mgmt.containsEdgeLabel(edgeLabel)) {
				mgmt.makeEdgeLabel(edgeLabel).make();
			}
			mgmt.commit();
	    } catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	        mgmt.rollback();
	    } 
	}
	
	/**
	 * 构建Mixed索引
	 * @param indexName
	 * @param elementType
	 * @return
	 */
	public TitanGraphIndex buildMixedIndex(String indexName, Class<? extends Element> elementType) {
		TitanManagement mgmt = openManagement();
		TitanGraphIndex graphIndex = null;
		try {
			graphIndex = buildMixedIndex(mgmt, indexName, elementType);
			mgmt.commit();
	    } catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	        mgmt.rollback();
	    } 
		return graphIndex;
	}
	
	/**
	 * 构建Mixed索引
	 * @param mgmt
	 * @param indexName
	 * @param elementType
	 * @return
	 */
	public TitanGraphIndex buildMixedIndex(TitanManagement mgmt, String indexName, Class<? extends Element> elementType) {
		return mgmt.containsGraphIndex(indexName) ? mgmt.getGraphIndex(indexName) :
			mgmt.buildIndex(indexName, elementType).buildMixedIndex("search");
	}
	
}
