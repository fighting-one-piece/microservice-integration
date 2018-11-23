package org.platform.modules.elastic.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticV2AbstractServiceImpl {
	
	private static Logger LOG = LoggerFactory.getLogger(ElasticV2AbstractServiceImpl.class);
	
	public static final String I_ATTRIBUTES = "i_attributes";
	public static final String NI_ATTRIBUTES = "ni_attributes";

	/** ES INDEX */
	protected static Set<String> indices = new HashSet<String>();
	
	/** ES INDEX TYPES 映射关系*/
	protected static Map<String, Set<String>> index_types_mapping = new HashMap<>();
	
	/** ES TYPE ATTRIBUTES 映射关系*/
	protected static Map<String, Map<String, Set<String>>> type_attributes_mapping = 
			new HashMap<String, Map<String, Set<String>>>();
	
	protected static Map<String, String> type_time_mapping = new HashMap<>();
	
	@PostConstruct
	public void postConstruct() {
		initESIndicesTypesAttributesCache("elastic/search_attributes_1.txt");
		initESIndicesTypesAttributesCache("elastic/search_attributes_2.txt");
		/**
		initESTypeTimeMapping();
		**/
	}
	
	protected String[] extractIndicesAndTypes(String[] qindices, String[] qtypes) {
		int qindicesLength = qindices.length, qtypesLength = qtypes.length;
		if (qindicesLength != qtypesLength) return new String[0];
		String[] indicesTypes = new String[qindicesLength + qtypesLength];
		for (int i = 0, j = 0, len = qindicesLength; i < len; i++) {
			indicesTypes[j++] = qindices[i];
			indicesTypes[j++] = qtypes[i];
		}
		return indicesTypes;
	}
	
	protected String[] judgeAndExtractIndicesAndTypes(String[] qindices, String[] qtypes) {
		List<String> indicesTypes = new ArrayList<String>();
		for (int i = 0, iLen = qindices.length; i < iLen; i++) {
			String index = qindices[i];
			if (!indices.contains(index)) continue;
			for (int j = 0, jLen = qtypes.length; j < jLen; j++) {
				String type = qtypes[j];
				if (index_types_mapping.get(index).contains(type)) {
					indicesTypes.add(index);
					indicesTypes.add(type);
				}
			}
		}
		return indicesTypes.toArray(new String[0]);
	}
	
	protected String getTypeTimeField(String type) {
		return type_time_mapping.get(type);
	}
	
	private void initESIndicesTypesAttributesCache(String fileName) {
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = ElasticV2AbstractServiceImpl.class.getClassLoader().getResourceAsStream(fileName);
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while (null != (line = br.readLine())) {
				String[] columns = line.split(",");
				String index = columns[0];
				String type = columns[1];
				indices.add(index);
				Set<String> types = index_types_mapping.get(index);
				if (null == types) {
					types = new HashSet<String>();
					index_types_mapping.put(index, types);
				}
				types.add(type);
				String itype = index + "_" + type;
				Map<String, Set<String>> attributes = type_attributes_mapping.get(itype);
				if (null == attributes) {
					attributes = new HashMap<String, Set<String>>();
					type_attributes_mapping.put(itype, attributes);
				}
				Set<String> iattributes = attributes.get(I_ATTRIBUTES);
				if (null == iattributes) {
					iattributes = new HashSet<String>();
					attributes.put(I_ATTRIBUTES, iattributes);
				}
				Set<String> niattributes = attributes.get(NI_ATTRIBUTES);
				if (null == niattributes) {
					niattributes = new HashSet<String>();
					attributes.put(NI_ATTRIBUTES, niattributes);
				}
				String attribute = columns[2];
				int identity = Integer.parseInt(columns[4]);
				if (identity == 1) {
					niattributes.add(attribute);
				} else if (identity == 2) {
					iattributes.add(attribute);
				} else if (identity == 3) {
					iattributes.add(attribute);
					niattributes.add(attribute);
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != br) br.close();
				if (null != in) in.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void initESTypeTimeMapping() {
		type_time_mapping.put("house", "c10");
		type_time_mapping.put("car", "d18,d9");
		type_time_mapping.put("contact", "d6");
		type_time_mapping.put("telecom", "c15");
		type_time_mapping.put("cybercafe", "d13");
		type_time_mapping.put("finance", "c10,c9");
		type_time_mapping.put("hotel", "c105,c111");
		type_time_mapping.put("bocai", "d19,c8,c9");
		type_time_mapping.put("qqdata", "loginTime");
		type_time_mapping.put("socialsecurity", "c9");
		type_time_mapping.put("oldweakyoung", "d5,d6");
		type_time_mapping.put("qqqundata", "createDate");
		type_time_mapping.put("resume", "d15,d2,c13,c46");
		type_time_mapping.put("accumulationfund", "d12,d11");
		type_time_mapping.put("humanresources", "c8,c17,c13");
		type_time_mapping.put("business", "setupDate,openDate");
		type_time_mapping.put("logistics", "orderDate,orderTime,begainTime,endTime");
	}
	
}
