package org.cisiondata.modules.qqrelation.utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ESClient {
	
	private TransportClient client = null;
	
	private ESClient() {
		initClient();
	}
	
	private static class ESClientHolder {
		private static final ESClient INSTANCE = new ESClient();
	}
	
	public static final ESClient getInstance() {
		return ESClientHolder.INSTANCE;
	}
	
	public Client getClient() {
		if (null == client) initClient();
		return client;
	}
	
	public void closeClient(Client client) {
		client.close();
	}
	
	private void initClient() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "cisiondata-graphdb")
				.put("client.tansport.sniff", true).build(); 
		client = new TransportClient(settings);
        List<EsServerAddress> serverAddress = new ArrayList<EsServerAddress>();
        serverAddress.add(new EsServerAddress("192.168.0.15", 19030));
        serverAddress.add(new EsServerAddress("192.168.0.16", 19030));
        serverAddress.add(new EsServerAddress("192.168.0.17", 19030));
		for (EsServerAddress address : serverAddress) {
			client.addTransportAddress(new InetSocketTransportAddress(
					new InetSocketAddress(address.getHost(), address.getPort())));
		}
	}
	
}

