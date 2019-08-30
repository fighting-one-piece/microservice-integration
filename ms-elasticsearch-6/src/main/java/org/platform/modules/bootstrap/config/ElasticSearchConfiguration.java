package org.platform.modules.bootstrap.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.platform.modules.elastic.utils.ElasticClient;
import org.platform.modules.elastic.utils.EsServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.floragunn.searchguard.ssl.SearchGuardSSLPlugin;
import com.floragunn.searchguard.ssl.util.SSLConfigConstants;

@Configuration
public class ElasticSearchConfiguration {
	
	private Logger LOG = LoggerFactory.getLogger(ElasticSearchConfiguration.class);

	@Value("${es.cluster.name}")
	private String clusterName = null;
	
	@Value("${es.cluster.nodes}")
	private String clusterNodesTxt = null;
	
	@Value("${es.cluster.rest.nodes}")
	private String clusterRestNodesTxt = null;
	
	@Value("${es.searchGuard.keystore-jks}")
	private String keystoreJks = null;
	
	@Value("${es.searchGuard.keystore-password}")
	private String keystorePassword = null;
	
	@Value("${es.searchGuard.truststore-jks}")
	private String truststoreJks = null;
	
	@Value("${es.searchGuard.truststore-password}")
	private String truststorePassword = null;

	@Value("${es.searchGuard.enforce-hostname-verification:false}")
	private Boolean enforceHostnameVerification = null;
	
	@Bean(name = "transportClient")
    public TransportClient transportClient() {
		if (StringUtils.isBlank(clusterName)) throw new RuntimeException("cluster name is not null");
		LOG.info("Elastic Search Cluster Nodes: " + clusterNodesTxt);
		String[] clusterNodes = clusterNodesTxt.indexOf(",") == -1 ? 
			new String[]{clusterNodesTxt} : clusterNodesTxt.split(",");
		List<EsServerAddress> esServerAddressList = new ArrayList<EsServerAddress>();
		for (int i = 0, len = clusterNodes.length; i < len; i++) {
			String clusterNode = clusterNodes[i];
			if (clusterNode.indexOf(":") == -1) continue;
			String[] ipAndPort = clusterNode.split(":");
			esServerAddressList.add(new EsServerAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
		}
		String tmpDir = System.getProperty("java.io.tmpdir");
	    LOG.info("tmp dir: {}", tmpDir);
	    String tmpKeystoreJks = tmpDir + File.separator + "keystore.jks";
	    String tmpTruststoreJks = tmpDir + File.separator + "truststore.jks";
	    InputStream keystoreJksIn = null, truststoreJksIn = null;
	    OutputStream keystoreJksOut = null, truststoreJksOut = null;
	    try {
	    	keystoreJksIn = ElasticClient.class.getClassLoader().getResourceAsStream(keystoreJks);
	    	truststoreJksIn = ElasticClient.class.getClassLoader().getResourceAsStream(truststoreJks);
	    	keystoreJksOut = new FileOutputStream(tmpKeystoreJks);
	    	truststoreJksOut = new FileOutputStream(tmpTruststoreJks);
	    	IOUtils.copy(keystoreJksIn, keystoreJksOut);
	    	IOUtils.copy(truststoreJksIn, truststoreJksOut);
	    } catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	    } finally {
	    	try {
		    	if (null != keystoreJksIn) keystoreJksIn.close();
		    	if (null != truststoreJksIn) truststoreJksIn.close();
		    	if (null != keystoreJksOut) keystoreJksOut.close();
		    	if (null != truststoreJksOut) truststoreJksOut.close();
	    	} catch (Exception e) {
		    	LOG.error(e.getMessage(), e);
		    }
	    }
		Settings settings = Settings.builder().put("path.home", ".")
				.put("cluster.name", clusterName).put("client.transport.sniff", true)
				.put(SSLConfigConstants.SEARCHGUARD_SSL_HTTP_ENABLED, true)
	            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_FILEPATH, tmpKeystoreJks)
	            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_KEYSTORE_PASSWORD, keystorePassword)
	            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_FILEPATH, tmpTruststoreJks)
	            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_TRUSTSTORE_PASSWORD, truststorePassword)
	            .put(SSLConfigConstants.SEARCHGUARD_SSL_TRANSPORT_ENFORCE_HOSTNAME_VERIFICATION, enforceHostnameVerification)
				.build();
		TransportClient transportClient = new PreBuiltTransportClient(settings, SearchGuardSSLPlugin.class);
		for (EsServerAddress esServerAddress : esServerAddressList) {
			transportClient.addTransportAddress(new TransportAddress(
				new InetSocketAddress(esServerAddress.getHost(), esServerAddress.getPort())));
		}
		return transportClient;
	}
	
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
		return new RestHighLevelClient(RestClient.builder(httpHosts));
	}
	
}
