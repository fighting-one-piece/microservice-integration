package org.platform.modules.elastic.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

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

public class ElasticV3AbstractServiceImpl {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());

	/** ES INDEX */
	protected static Set<String> indices = new HashSet<String>();
	/** ES TYPE */
	protected static Set<String> types = new HashSet<String>();
	
	/** ES TYPE INDEX 映射关系*/
	protected static Map<String, String> type_index_mapping = new HashMap<String, String>();
	
	/** ES TYPE ATTRIBUTES 映射关系*/
	protected static Map<String, Set<String>> type_attributes_mapping = new HashMap<String, Set<String>>();
	
	/** 属性字段过滤*/
	protected static Set<String> filter_attributes = new HashSet<String>();
	
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
		filter_attributes.add("c135");
		filter_attributes.add("c136");
		filter_attributes.add("c137");
		filter_attributes.add("c138");
		filter_attributes.add("c139");
		filter_attributes.add("insertTime");
		filter_attributes.add("updateTime");
		filter_attributes.add("inputPerson");
	}
	
	@SuppressWarnings("unchecked")
	private void initESIndicesTypesAttributesCache() {
		indices = new HashSet<String>();
		types = new HashSet<String>();
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
				if (index.startsWith(".marvel-es") || index.endsWith("-v1")) continue;
				indices.add(index);
				ImmutableOpenMap<String, MappingMetaData> immutableOpenMap = objectObjectCursor.value;
				ObjectLookupContainer<String> keys = immutableOpenMap.keys();
				Iterator<ObjectCursor<String>> keysIterator = keys.iterator();
				while(keysIterator.hasNext()) {
					String type = keysIterator.next().value;
					if("financial".equals(index) && "business".equals(type)) continue;
					types.add(type);
					type_index_mapping.put(type, index);
					Set<String> attributes = type_attributes_mapping.get(type);
					if (null == attributes) attributes = new HashSet<String>();
					MappingMetaData mappingMetaData = immutableOpenMap.get(type);
					Map<String, Object> mapping = mappingMetaData.getSourceAsMap();
					if (mapping.containsKey("properties")) {
						Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
						for (String attribute : properties.keySet()) {
							if (filter_attributes.contains(attribute)) continue;
							Map<String, Object> props = (Map<String, Object>) properties.get(attribute);
							Object typeObject = props.get("type");
							if (null != typeObject && "string".equals(typeObject)) {
								attributes.add(attribute);
							}
						}
					}
					type_attributes_mapping.put(type, attributes);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
}


