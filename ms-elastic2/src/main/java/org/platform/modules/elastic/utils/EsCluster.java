package org.platform.modules.elastic.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EsCluster {
	
	@Value("${es.cluster.name}")
	private String clusterName = null;

	@Value("${es.cluster.nodes}")
	private String clusterNodes = null;

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getClusterNodes() {
		return clusterNodes;
	}

	public void setClusterNodes(String clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	
	
	

}
