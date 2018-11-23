package org.platform.utils.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class JedisClusterFactory implements FactoryBean<JedisCluster>, InitializingBean {

	private final Logger LOG = LoggerFactory.getLogger(JedisClusterFactory.class);

	private String address = null;
	private Integer timeout = null;
	private Integer maxRedirections = null;
	private JedisCluster jedisCluster = null;
	private GenericObjectPoolConfig genericObjectPoolConfig = null;
	private Pattern pattern = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

	@Override
	public JedisCluster getObject() throws Exception {
		return jedisCluster;
	}

	@Override
	public Class<? extends JedisCluster> getObjectType() {
		return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Set<HostAndPort> hostAndPorts = this.parseHostAndPort();
		if (hostAndPorts.size() == 0) return;
		jedisCluster = new JedisCluster(hostAndPorts, timeout, maxRedirections, genericObjectPoolConfig);
	}
	
	private Set<HostAndPort> parseHostAndPort() {
		Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
		try {
			if (null == this.address || "".equals(this.address)) return hostAndPorts;
			String[] addresses = this.address.indexOf(",") == -1 ? new String[]{this.address} : this.address.split(",");
			for (int i = 0, len = addresses.length; i < len; i++) {
				String clusterAddress = addresses[i];
				boolean isIpPort = pattern.matcher(clusterAddress).matches();
				if (!isIpPort) {
					throw new IllegalArgumentException("ip or port error");
				}
				String[] ipAndPort = clusterAddress.split(":");
				HostAndPort hostAndPort = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
				hostAndPorts.add(hostAndPort);
			}
		} catch (Exception e) {
			LOG.error("parse redis cluster config failure!", e);
		}
		return hostAndPorts;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
		this.genericObjectPoolConfig = genericObjectPoolConfig;
	}

}
