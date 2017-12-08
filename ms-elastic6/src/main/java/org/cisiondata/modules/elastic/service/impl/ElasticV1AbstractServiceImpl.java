package org.cisiondata.modules.elastic.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.cisiondata.modules.elastic.utils.ElasticClient;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectLookupContainer;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;

public class ElasticV1AbstractServiceImpl {
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());

	/** ES INDEX */
	protected static Set<String> indices = null;
	/** ES TYPE */
	protected static Set<String> types = null;
	
	/** ES 搜索属性字段*/
	protected static Set<String> name_attributes = null;
	protected static Set<String> phone_attributes = null;
	protected static Set<String> idcard_attributes = null;
	protected static Set<String> chinese_attributes = null;
	protected static Set<String> identity_attributes = null;
	protected static Set<String> location_attributes = null;
	protected static Set<String> all_attributes = null;
	
	/** ES TYPE INDEX 映射关系*/
	protected static Map<String, String> type_index_mapping = null;
	
	/** ES INDEX TYPE 映射关系*/
	protected static Map<String, List<String>> index_types_mapping = null;
	
	/** ES INDEX TYPE ATTRIBUTES 映射关系*/
	protected static Map<String, Map<String, Set<String>>> index_type_attributes_mapping = null;
	
	/** 属性字段过滤*/
	protected static Set<String> filter_attributes = null;
	/** 标识属性字段过滤*/
	protected static Set<String> identity_filter_attributes = null;
	
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
		all_attributes = new HashSet<String>();
		name_attributes = new HashSet<String>();
		phone_attributes = new HashSet<String>();
		idcard_attributes = new HashSet<String>();
		chinese_attributes = new HashSet<String>();
		identity_attributes = new HashSet<String>();
		location_attributes = new HashSet<String>();
		type_index_mapping = new HashMap<String, String>();
		index_types_mapping = new HashMap<String, List<String>>();
		index_type_attributes_mapping = new HashMap<String, Map<String, Set<String>>>();
		filter_attributes = new HashSet<String>();
		filter_attributes.add("site");
		filter_attributes.add("cnote");
		filter_attributes.add("insertTime");
		filter_attributes.add("updateTime");
		filter_attributes.add("inputPerson");
		identity_filter_attributes = new HashSet<String>();
		identity_filter_attributes.add("phoneAssociation");
		identity_filter_attributes.add("phonePackage");
		identity_filter_attributes.add("phoneModel");
		identity_filter_attributes.add("phoneState");
		identity_filter_attributes.add("phonePrice");
		identity_filter_attributes.add("phoneType");
		identity_filter_attributes.add("qqPoint");
		identity_filter_attributes.add("qqCoin");
		identity_filter_attributes.add("mastQQ");
		identity_filter_attributes.add("cityId");
		identity_filter_attributes.add("carName");
		identity_filter_attributes.add("cardName");
		identity_filter_attributes.add("bankName");
		identity_filter_attributes.add("payName");
		identity_filter_attributes.add("provinceId");
		identity_filter_attributes.add("ipAddress");
		identity_filter_attributes.add("companyNo");
		identity_filter_attributes.add("companyCode");
		identity_filter_attributes.add("companyType");
		identity_filter_attributes.add("companyNature");
		identity_filter_attributes.add("companyZipcode");
		identity_filter_attributes.add("emailSuffix");
		identity_filter_attributes.add("accountType");
		identity_filter_attributes.add("gameAreaName");
		identity_filter_attributes.add("manufactoryName");
		identity_filter_attributes.add("registionAccount");
	}
	
	private void filterAttributeCache(String attribute, Map<String, Set<String>> indexTypeAttributes) {
		String attributeLowerCase = attribute.toLowerCase();
		if (identity_filter_attributes.contains(attribute)) return;
		if (attributeLowerCase.indexOf("name") != -1 && 
				attributeLowerCase.indexOf("alias") == -1) {
			name_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("call") != -1) {
			phone_attributes.add(attribute);
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("phone") != -1) {
			phone_attributes.add(attribute);
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("clientcode") != -1) {
			phone_attributes.add(attribute);
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("idcode") != -1) {
			phone_attributes.add(attribute);
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("email") != -1) {
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("idcard") != -1) {
			idcard_attributes.add(attribute);
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("qq") != -1) {
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("account") != -1) {
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("password") != -1) {
			identity_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("address") != -1) {
			location_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("company") != -1 && 
				attributeLowerCase.indexOf("alias") == -1) {
			location_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("province") != -1) {
			location_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("city") != -1) {
			location_attributes.add(attribute);
		} else if (attributeLowerCase.indexOf("county") != -1) {
			location_attributes.add(attribute);
		}
		chinese_attributes.addAll(name_attributes);
		chinese_attributes.addAll(location_attributes);
		all_attributes.addAll(name_attributes);
		all_attributes.addAll(identity_attributes);
		all_attributes.addAll(location_attributes);
		Set<String> it_identity_attributes = indexTypeAttributes.get("identity_attributes");
		if (null == it_identity_attributes) {
			it_identity_attributes = new HashSet<String>();
			indexTypeAttributes.put("identity_attributes", it_identity_attributes);
		}
		if (identity_attributes.contains(attribute)) it_identity_attributes.add(attribute);
		Set<String> it_chinese_attributes = indexTypeAttributes.get("chinese_attributes");
		if (null == it_chinese_attributes) {
			it_chinese_attributes = new HashSet<String>();
			indexTypeAttributes.put("chinese_attributes", it_chinese_attributes);
		}
		if (chinese_attributes.contains(attribute)) it_chinese_attributes.add(attribute);
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
				if (index.startsWith(".marvel-es")) continue;
				indices.add(index);
				List<String> indexTypes = index_types_mapping.get(index);
				if (null == indexTypes) {
					indexTypes = new ArrayList<String>();
					index_types_mapping.put(index, indexTypes);
				}
				ImmutableOpenMap<String, MappingMetaData> immutableOpenMap = objectObjectCursor.value;
				ObjectLookupContainer<String> keys = immutableOpenMap.keys();
				Iterator<ObjectCursor<String>> keysIterator = keys.iterator();
				while(keysIterator.hasNext()) {
					String type = keysIterator.next().value;
					types.add(type);
					indexTypes.add(type);
					type_index_mapping.put(type, index);
					String indexType = index + type;
					Map<String, Set<String>> index_type_attributes = index_type_attributes_mapping.get(indexType);
					if (null == index_type_attributes_mapping.get(indexType)) {
						index_type_attributes = new HashMap<String, Set<String>>();
						index_type_attributes_mapping.put(indexType, index_type_attributes);
					}
					MappingMetaData mappingMetaData = immutableOpenMap.get(type);
					Map<String, Object> mapping = mappingMetaData.getSourceAsMap();
					if (mapping.containsKey("properties")) {
						Map<String, Object> properties = (Map<String, Object>) mapping.get("properties");
						for (String attribute : properties.keySet()) {
							filterAttributeCache(attribute, index_type_attributes);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} 
	}
	
}


