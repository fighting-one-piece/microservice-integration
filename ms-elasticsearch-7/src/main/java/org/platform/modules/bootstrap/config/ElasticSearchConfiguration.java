package org.platform.modules.bootstrap.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfiguration {
	
	@Value("${es.cluster.name}")
	private String clusterName = null;
	
	@Value("${es.cluster.nodes}")
	private String clusterNodesTxt = null;
	
	@Value("${es.cluster.rest.nodes}")
	private String clusterRestNodesTxt = null;
	
	private static final int connectTimeOut = 1000;
	private static final int socketTimeOut = 30000; 
	private static final int connectionRequestTimeOut = 500;
	 
	private static final int maxConnectTotal = 100;
	private static final int maxConnectPerRoute = 100;
	
	@Bean(name = "restHighLevelClient")
	public RestHighLevelClient restHighLevelClient() {
		String[] clusterRestNodes = clusterRestNodesTxt.indexOf(",") == -1 ? 
			new String[]{clusterRestNodesTxt} : clusterRestNodesTxt.split(",");
		HttpHost[] httpHosts = new HttpHost[clusterRestNodes.length];
		for (int i = 0, len = clusterRestNodes.length; i < len; i++) {
			String clusterRestNode = clusterRestNodes[i];
			if (clusterRestNode.indexOf(":") == -1) continue;
			String[] ipAndPort = clusterRestNode.split(":");
			httpHosts[i] = new HttpHost(ipAndPort[0], Integer.parseInt(ipAndPort[1]), "http");
		}
		RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);
		restClientBuilder.setRequestConfigCallback(new RequestConfigCallback() {

			@Override
			public Builder customizeRequestConfig(Builder requestConfigBuilder) {
				requestConfigBuilder.setConnectTimeout(connectTimeOut);
				requestConfigBuilder.setSocketTimeout(socketTimeOut);
				requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
				return requestConfigBuilder;
			}
			
		});
		restClientBuilder.setHttpClientConfigCallback(new HttpClientConfigCallback() {

			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				httpClientBuilder.setMaxConnTotal(maxConnectTotal);
				httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
				return httpClientBuilder;
			}
			
		});
		return new RestHighLevelClient(restClientBuilder);
	}
	
}
