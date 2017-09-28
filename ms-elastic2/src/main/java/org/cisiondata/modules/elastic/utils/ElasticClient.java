package org.cisiondata.modules.elastic.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.shield.ShieldPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticClient {
	
	private static Logger LOG = LoggerFactory.getLogger(ElasticClient.class);
	
	private static final String CONFIG_FILE = "application-%s.properties";
	
	private TransportClient client = null;
	
	private ElasticClient() {
		initClient();
	}
	
	private static class ElasticClientHolder {
		private static final ElasticClient INSTANCE = new ElasticClient();
	}
	
	public static final ElasticClient getInstance() {
		return ElasticClientHolder.INSTANCE;
	}
	
	public Client getClient() {
		if (null == client) initClient();
		return client;
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
			in = ElasticClient.class.getClassLoader().getResourceAsStream(String.format(CONFIG_FILE, activeEnv));
			properties.load(in);
			String clusterName = properties.getProperty("es.cluster.name");
			if (StringUtils.isBlank(clusterName)) throw new RuntimeException("cluster name is not null");
			String clusterNodesTxt = properties.getProperty("es.cluster.nodes");
			LOG.info("Elastic Search Cluster Nodes: " + clusterNodesTxt);
			String[] clusterNodes = clusterNodesTxt.indexOf(",") == -1 ? 
				new String[]{clusterNodesTxt} : clusterNodesTxt.split(",");
			List<EsServerAddress> esServerAddress = new ArrayList<EsServerAddress>();
			for (int i = 0, len = clusterNodes.length; i < len; i++) {
				String clusterNode = clusterNodes[i];
				if (clusterNode.indexOf(":") == -1) continue;
				String[] ipAndPort = clusterNode.split(":");
				esServerAddress.add(new EsServerAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
			}
			Settings settings = Settings.builder().put("cluster.name", clusterName)
					.put("shield.user","guest:@#guest123").put("client.transport.sniff", true).build();
			client = TransportClient.builder().addPlugin(ShieldPlugin.class).settings(settings).build();
			for (EsServerAddress address : esServerAddress) {
				client.addTransportAddress(new InetSocketTransportAddress(
					new InetSocketAddress(address.getHost(), address.getPort())));
			}
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

