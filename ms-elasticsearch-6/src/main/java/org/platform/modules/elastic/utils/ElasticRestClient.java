package org.platform.modules.elastic.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticRestClient {
	
	private static Logger LOG = LoggerFactory.getLogger(ElasticRestClient.class);
	
	private static final String CONFIG_FILE = "application-%s.properties";
	
	private RestHighLevelClient restHighLevelClient = null;
	
	private ElasticRestClient() {
		initClient();
	}
	
	private static class ElasticRestClientHolder {
		private static final ElasticRestClient INSTANCE = new ElasticRestClient();
	}
	
	public static final ElasticRestClient getInstance() {
		return ElasticRestClientHolder.INSTANCE;
	}
	
	public RestHighLevelClient getClient() {
		if (null == restHighLevelClient) initClient();
		return restHighLevelClient;
	}
	
	public void closeClient(Client client) {
		client.close();
	}
	
	private void initClient() {
		Properties properties = new Properties();
		InputStream in = null;
		try {
			String activeEnv = System.getProperty("spring.profiles.active");
			activeEnv = StringUtils.isBlank(activeEnv) ? "development" : activeEnv;
			in = ElasticRestClient.class.getClassLoader().getResourceAsStream(String.format(CONFIG_FILE, activeEnv));
			properties.load(in);
			String clusterName = properties.getProperty("es.cluster.name");
			if (StringUtils.isBlank(clusterName)) throw new RuntimeException("cluster name is not null");
			String clusterRestNodesTxt = properties.getProperty("es.cluster.rest.nodes");
			LOG.info("Elastic Search Cluster Rest Nodes: " + clusterRestNodesTxt);
			String[] clusterRestNodes = clusterRestNodesTxt.indexOf(",") == -1 ? 
				new String[]{clusterRestNodesTxt} : clusterRestNodesTxt.split(",");
			HttpHost[] httpHosts = new HttpHost[clusterRestNodes.length];
			for (int i = 0, len = clusterRestNodes.length; i < len; i++) {
				String clusterRestNode = clusterRestNodes[i];
				if (clusterRestNode.indexOf(":") == -1) continue;
				String[] ipAndPort = clusterRestNode.split(":");
				httpHosts[i] = new HttpHost(ipAndPort[0], Integer.parseInt(ipAndPort[1]), "http");
			}
			restHighLevelClient = new RestHighLevelClient(RestClient.builder(httpHosts));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if (null != in) in.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
}

