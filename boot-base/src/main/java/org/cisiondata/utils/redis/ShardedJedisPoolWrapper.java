package org.cisiondata.utils.redis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

public class ShardedJedisPoolWrapper {
    
    private static final Logger LOG = LoggerFactory.getLogger(ShardedJedisPoolWrapper.class);
    
    public static final int REDIS_TIMEOUT = 1000;
    
    private ShardedJedisPool jedisPool = null;
    
    public ShardedJedisPoolWrapper(GenericObjectPoolConfig config, String address) {
    	LOG.info("Redis started on address: " + address);
        jedisPool = new ShardedJedisPool(config, buildShardInfos(address, ""));
    }
    
    public ShardedJedisPoolWrapper(GenericObjectPoolConfig config, String address, String password) {
    	LOG.info("Redis started on address: " + address);
        jedisPool = new ShardedJedisPool(config, buildShardInfos(address, password));
    }
    
    public ShardedJedisPool getJedisPool() {
        return this.jedisPool;
    }
 
    private static List<JedisShardInfo> buildShardInfos(String address, String password) {
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        for(String addr : address.split(" ")) {
            String[] parts = addr.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            JedisShardInfo info = new JedisShardInfo(host, port, REDIS_TIMEOUT);
            if (!StringUtils.isEmpty(password)) {
                info.setPassword(password);
            }
            shards.add(info);
        }
        return shards;
    }
}
