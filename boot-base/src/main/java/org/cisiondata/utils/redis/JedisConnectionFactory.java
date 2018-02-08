package org.cisiondata.utils.redis;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.redis.ExceptionTranslationStrategy;
import org.springframework.data.redis.PassThroughExceptionTranslationStrategy;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConverters;
import org.springframework.data.redis.connection.jedis.JedisSentinelConnection;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;

public class JedisConnectionFactory implements InitializingBean, DisposableBean, RedisConnectionFactory {

	private final static Logger LOG = LoggerFactory.getLogger(JedisConnectionFactory.class);

	private static final ExceptionTranslationStrategy EXCEPTION_TRANSLATION = new PassThroughExceptionTranslationStrategy(
			JedisConverters.exceptionConverter());

	private static final Method SET_TIMEOUT_METHOD;
	private static final Method GET_TIMEOUT_METHOD;

	static {
		Method setTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "setTimeout", int.class);
		if (setTimeoutMethodCandidate == null) {
			setTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "setSoTimeout", int.class);
		}
		SET_TIMEOUT_METHOD = setTimeoutMethodCandidate;
		Method getTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "getTimeout");
		if (getTimeoutMethodCandidate == null) {
			getTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "getSoTimeout");
		}
		GET_TIMEOUT_METHOD = getTimeoutMethodCandidate;
	}

	private JedisShardInfo shardInfo = null;
	private String hostName = "localhost";
	private int port = Protocol.DEFAULT_PORT;
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	private String password = null;
	private boolean usePool = true;
	private boolean useSsl = false;
	private Pool<Jedis> pool = null;
	private JedisPoolConfig poolConfig = new JedisPoolConfig();
	private int dbIndex = 0;
	private String clientName = null;
	private boolean convertPipelineAndTxResults = true;
	private RedisSentinelConfiguration sentinelConfig = null;
	private JedisCluster jedisCluster = null;

	public JedisConnectionFactory() {
	}

	public JedisConnectionFactory(JedisShardInfo shardInfo) {
		this.shardInfo = shardInfo;
	}

	public JedisConnectionFactory(JedisPoolConfig poolConfig) {
		this((RedisSentinelConfiguration) null, poolConfig);
	}

	public JedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
		this(sentinelConfig, null);
	}

	public JedisConnectionFactory(RedisSentinelConfiguration sentinelConfig, JedisPoolConfig poolConfig) {
		this.sentinelConfig = sentinelConfig;
		this.poolConfig = poolConfig != null ? poolConfig : new JedisPoolConfig();
	}

	protected Jedis fetchJedisConnector() {
		try {
			if (usePool && pool != null) {
				return pool.getResource();
			}
			Jedis jedis = new Jedis(getShardInfo());
			jedis.connect();
			potentiallySetClientName(jedis);
			return jedis;
		} catch (Exception ex) {
			throw new RedisConnectionFailureException("Cannot get Jedis connection", ex);
		}
	}

	/**
	 * Post process a newly retrieved connection. Useful for decorating or
	 * executing initialization commands on a new connection. This
	 * implementation simply returns the connection.
	 * 
	 * @param connection
	 * @return processed connection
	 */
	protected JedisConnection postProcessConnection(JedisConnection connection) {
		return connection;
	}

	@Override
	public void afterPropertiesSet() {
		if (shardInfo == null) {
			shardInfo = new JedisShardInfo(hostName, port);
			if (StringUtils.hasLength(password)) {
				shardInfo.setPassword(password);
			}
			if (timeout > 0) {
				setTimeoutOn(shardInfo, timeout);
			}
		}
		if (usePool) {
			this.pool = createPool();
		}
	}

	private Pool<Jedis> createPool() {
		if (isRedisSentinelAware()) {
			return createRedisSentinelPool(this.sentinelConfig);
		}
		return createRedisPool();
	}

	protected Pool<Jedis> createRedisSentinelPool(RedisSentinelConfiguration config) {
		return new JedisSentinelPool(config.getMaster().getName(), convertToJedisSentinelSet(config.getSentinels()),
				getPoolConfig() != null ? getPoolConfig() : new JedisPoolConfig(), getTimeoutFrom(getShardInfo()),
				getShardInfo().getPassword(), Protocol.DEFAULT_DATABASE, clientName);
	}

	protected Pool<Jedis> createRedisPool() {
		return new JedisPool(getPoolConfig(), getShardInfo().getHost(), getShardInfo().getPort(),
				getTimeoutFrom(getShardInfo()), getShardInfo().getPassword(), 
				Protocol.DEFAULT_DATABASE, clientName, useSsl);
	}

	public void destroy() {
		if (usePool && pool != null) {
			try {
				pool.destroy();
			} catch (Exception ex) {
				LOG.warn("Cannot properly close Jedis pool", ex);
			}
			pool = null;
		}
	}

	public RedisConnection getConnection() {
		Jedis jedis = fetchJedisConnector();
		JedisConnection connection = new JedisConnection(jedis, usePool ? pool : null, dbIndex);
		connection.setConvertPipelineAndTxResults(convertPipelineAndTxResults);
		return postProcessConnection(connection);
	}

	@Override
	public RedisClusterConnection getClusterConnection() {
		if (jedisCluster == null) {
			throw new InvalidDataAccessApiUsageException("Cluster is not configured!");
		}
		return new JedisClusterConnection(jedisCluster);
	}

	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		return EXCEPTION_TRANSLATION.translate(ex);
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}

	public boolean isUseSsl() {
		return useSsl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public JedisShardInfo getShardInfo() {
		return shardInfo;
	}

	public void setShardInfo(JedisShardInfo shardInfo) {
		this.shardInfo = shardInfo;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean getUsePool() {
		return usePool;
	}

	public void setUsePool(boolean usePool) {
		this.usePool = usePool;
	}

	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}

	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

	public int getDatabase() {
		return dbIndex;
	}

	/**
	 * Sets the index of the database used by this connection factory. Default
	 * is 0.
	 * 
	 * @param index
	 *            database index.
	 */
	public void setDatabase(int index) {
		Assert.isTrue(index >= 0, "invalid DB index (a positive index required)");
		this.dbIndex = index;
	}

	public String getClientName() {
		return clientName;
	}

	/**
	 * Sets the client name used by this connection factory. Defaults to none
	 * which does not set a client name.
	 * 
	 * @param clientName
	 *            the client name.
	 * @since 1.8
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	/**
	 * Specifies if pipelined results should be converted to the expected data
	 * type. If false, results of {@link JedisConnection#closePipeline()} and
	 * {@link JedisConnection#exec()} will be of the type returned by the Jedis
	 * driver.
	 * 
	 * @return Whether or not to convert pipeline and tx results.
	 */
	public boolean getConvertPipelineAndTxResults() {
		return convertPipelineAndTxResults;
	}

	/**
	 * Specifies if pipelined results should be converted to the expected data
	 * type. If false, results of {@link JedisConnection#closePipeline()} and
	 * {@link JedisConnection#exec()} will be of the type returned by the Jedis
	 * driver.
	 * 
	 * @param convertPipelineAndTxResults
	 *            Whether or not to convert pipeline and tx results.
	 */
	public void setConvertPipelineAndTxResults(boolean convertPipelineAndTxResults) {
		this.convertPipelineAndTxResults = convertPipelineAndTxResults;
	}

	public boolean isRedisSentinelAware() {
		return sentinelConfig != null;
	}

	@Override
	public RedisSentinelConnection getSentinelConnection() {
		if (!isRedisSentinelAware()) {
			throw new InvalidDataAccessResourceUsageException("No Sentinels configured");
		}
		return new JedisSentinelConnection(getActiveSentinel());
	}

	private Jedis getActiveSentinel() {
		Assert.notNull(this.sentinelConfig, "SentinelConfig must not be null!");
		for (RedisNode node : this.sentinelConfig.getSentinels()) {
			Jedis jedis = new Jedis(node.getHost(), node.getPort());
			if (jedis.ping().equalsIgnoreCase("pong")) {
				potentiallySetClientName(jedis);
				return jedis;
			}
		}
		throw new InvalidDataAccessResourceUsageException("No Sentinel found");
	}

	private Set<String> convertToJedisSentinelSet(Collection<RedisNode> nodes) {
		if (CollectionUtils.isEmpty(nodes)) {
			return Collections.emptySet();
		}
		Set<String> convertedNodes = new LinkedHashSet<String>(nodes.size());
		for (RedisNode node : nodes) {
			if (node != null) {
				convertedNodes.add(node.asString());
			}
		}
		return convertedNodes;
	}

	private void potentiallySetClientName(Jedis jedis) {
		if (StringUtils.hasText(clientName)) {
			jedis.clientSetname(clientName);
		}
	}

	private void setTimeoutOn(JedisShardInfo shardInfo, int timeout) {
		ReflectionUtils.invokeMethod(SET_TIMEOUT_METHOD, shardInfo, timeout);
	}

	private int getTimeoutFrom(JedisShardInfo shardInfo) {
		return (Integer) ReflectionUtils.invokeMethod(GET_TIMEOUT_METHOD, shardInfo);
	}

}
