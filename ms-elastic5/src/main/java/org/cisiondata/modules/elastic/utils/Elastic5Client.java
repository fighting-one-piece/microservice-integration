package org.cisiondata.modules.elastic.utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class Elastic5Client {
	
	private TransportClient client = null;
	
	private Elastic5Client() {
		initClient();
	}
	
	private static class ESClientHolder {
		private static final Elastic5Client INSTANCE = new Elastic5Client();
	}
	
	public static final Elastic5Client getInstance() {
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
        Settings settings = Settings.builder().put("cluster.name", "cisiondata")
        		.put("client.transport.sniff", true).build();
        client = new PreBuiltTransportClient(settings);
        List<EsServerAddress> esServerAddress = new ArrayList<EsServerAddress>();
		esServerAddress.add(new EsServerAddress("192.168.0.115", 9300));
		for (EsServerAddress address : esServerAddress) {
			client.addTransportAddress(new InetSocketTransportAddress(
					new InetSocketAddress(address.getHost(), address.getPort())));
		}
	}
	
}

