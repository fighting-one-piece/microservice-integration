package org.cisiondata.modules.elastic.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
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
	/** ES TYPE */
	protected static Set<String> types = new HashSet<String>();
	
	/** ES INDEX TYPE 映射关系*/
	protected static Map<String, String> index_type_mapping = new HashMap<String, String>();
	
	/** ES TYPE INDEX 映射关系*/
	protected static Map<String, String> type_index_mapping = new HashMap<String, String>();
	
	/** ES TYPE ATTRIBUTES 映射关系*/
	protected static Map<String, Map<String, Set<String>>> type_attributes_mapping = 
			new HashMap<String, Map<String, Set<String>>>();
	
	/** ES 搜索属性字段*/
	protected static Set<String> i_attributes = new HashSet<String>();
	protected static Set<String> ni_attributes = new HashSet<String>();
	
	public ElasticV2AbstractServiceImpl() {
		initESIndicesTypesAttributesCache();
	}
	
	@PostConstruct
	public void postConstruct() {
		initESIndicesTypesAttributesCache();
	}
	
	private void initESIndicesTypesAttributesCache() {
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = ElasticV2AbstractServiceImpl.class.getClassLoader().getResourceAsStream("elastic/search_attributes.txt");
			br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while (null != (line = br.readLine())) {
				String[] columns = line.split(",");
				String index = columns[0];
				String type = columns[1];
				String attribute = columns[2];
				int identity = Integer.parseInt(columns[3]);
				indices.add(index);
				types.add(type);
				if (-1 != index.indexOf("-v")) {
					index_type_mapping.put(index, type);
				}
				type_index_mapping.put(type, index);
				Map<String, Set<String>> attributes = type_attributes_mapping.get(type);
				if (null == attributes) {
					attributes = new HashMap<String, Set<String>>();
					type_attributes_mapping.put(type, attributes);
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
				if (identity == 1) {
					iattributes.add(attribute);
					i_attributes.add(attribute);
				} else if (identity == 2) {
					niattributes.add(attribute);
					ni_attributes.add(attribute);
				} else if (identity == 3) {
					iattributes.add(attribute);
					niattributes.add(attribute);
					i_attributes.add(attribute);
					ni_attributes.add(attribute);
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
	
	public static void main(String[] args) {
		ElasticV2AbstractServiceImpl a = new ElasticV2AbstractServiceImpl();
		a.initESIndicesTypesAttributesCache();
		System.out.println("indices: " + indices.size());
		System.out.println("types: " + types.size());
		System.out.println("index_type_mapping: " + index_type_mapping);
		System.out.println("type_index_mapping: " + type_index_mapping);
		System.out.println("type_attributes_mapping: " + type_attributes_mapping.size());
		for (Map.Entry<String, Map<String, Set<String>>> entry : type_attributes_mapping.entrySet()) {
			System.err.println(entry.getKey());
			System.err.println(entry.getValue());
		}
	}
	
}
