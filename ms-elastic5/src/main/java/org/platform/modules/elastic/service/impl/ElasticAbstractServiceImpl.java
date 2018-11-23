package org.platform.modules.elastic.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.platform.modules.elastic.utils.ElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLookupContainer;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;

public class ElasticAbstractServiceImpl {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());

	/** ES INDEX */
	protected static Set<String> indices = null;
	/** ES TYPE */
	protected static Set<String> types = null;
	/** ES ATTRIBUTES I */
	protected static Set<String> i_attributes = null;
	/** ES ATTRIBUTES N */
	protected static Set<String> n_attributes = null;
	/** ES ATTRIBUTES A */
	protected static Set<String> a_attributes = null;
	/** ES ATTRIBUTES D */
	protected static Set<String> d_attributes = null;
	/** ES ATTRIBUTES O */
	protected static Set<String> o_attributes = null;
	
	/** ES INDEX TYPE 映射关系*/
	protected static Map<String, String> index_type_mapping = null;
	
	/** ES TYPE ATTRIBUTES 映射关系*/
	protected static Map<String, Set<String>> type_attributes_mapping = null;
	
	@PostConstruct
	public void postConstruct() {
		initAttributeCache();
		initESIndicesTypesAttributesCache();
	}
	
	protected String[] defaultIndices() {
		return indices.toArray(new String[0]);
	}
	
	protected String[] defaultTypes() {
		return types.toArray(new String[0]);
	}
	
	private void initAttributeCache() {
		indices = new HashSet<String>();
		types = new HashSet<String>();
		i_attributes = new HashSet<String>();
		n_attributes = new HashSet<String>();
		a_attributes = new HashSet<String>();
		d_attributes = new HashSet<String>();
		o_attributes = new HashSet<String>();
		index_type_mapping = new HashMap<String, String>();
		type_attributes_mapping = new HashMap<String, Set<String>>();
	}
	
	@SuppressWarnings("unchecked")
	private void initESIndicesTypesAttributesCache() {
		try {
			IndicesAdminClient indicesAdminClient = ElasticClient.getInstance().getClient().admin().indices();
			GetMappingsResponse getMappingsResponse = indicesAdminClient.getMappings(new GetMappingsRequest()).get();
			ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = 
					getMappingsResponse.getMappings();
			Iterator<ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>>> 
			mappingIterator = mappings.iterator();
			while (mappingIterator.hasNext()) {
				ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>>
				objectObjectCursor = mappingIterator.next();
				String index = objectObjectCursor.key;
				if (index.startsWith(".marvel-es")) continue;
				indices.add(index);
				ImmutableOpenMap<String, MappingMetaData> immutableOpenMap = objectObjectCursor.value;
				ObjectLookupContainer<String> keys = immutableOpenMap.keys();
				Iterator<ObjectCursor<String>> keysIterator = keys.iterator();
				while(keysIterator.hasNext()) {
					String type = keysIterator.next().value;
					types.add(type);
					index_type_mapping.put(index, type);
					MappingMetaData mappingMetaData = immutableOpenMap.get(type);
					Map<String, Object> mapping = mappingMetaData.getSourceAsMap();
					if (mapping.containsKey("properties")) {
						Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
						Set<String> attributes = new HashSet<String>();
						for (String attribute : properties.keySet()) {
							attributes.add(attribute);
							if (attribute.startsWith("i")) {
								i_attributes.add(attribute);
							} else if (attribute.startsWith("n")) {
								n_attributes.add(attribute);
							} else if (attribute.startsWith("a")) {
								a_attributes.add(attribute);
							} else if (attribute.startsWith("d")) {
								d_attributes.add(attribute);
							} else if (attribute.startsWith("o")) {
								o_attributes.add(attribute);
							}
						}
						type_attributes_mapping.put(type, attributes);
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
	protected boolean indicesExists(String... indices) {
		if (null == indices || indices.length == 0) return false;
		IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest(indices);
		IndicesExistsResponse indicesExistsResponse = ElasticClient.getInstance().getClient()
				.admin().indices().exists(indicesExistsRequest).actionGet();
		return indicesExistsResponse.isExists();
	}
	
}


