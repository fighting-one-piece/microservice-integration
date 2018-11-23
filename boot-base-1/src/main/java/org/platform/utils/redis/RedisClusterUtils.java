package org.platform.utils.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.platform.utils.reflect.ReflectUtils;
import org.platform.utils.serde.SerializerUtils;
import org.platform.utils.spring.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

public class RedisClusterUtils {
	
	private Logger LOG = LoggerFactory.getLogger(RedisClusterUtils.class);
	
	private JedisCluster jedisCluster = null;

	private RedisClusterUtils() {
		this.jedisCluster = (JedisCluster) SpringBeanFactory.getBean("jedisCluster");
	}

	private static class RedisClusterUtilsHolder {
		public static RedisClusterUtils INSTANCE = new RedisClusterUtils();
	}

	public static RedisClusterUtils getInstance() {
		return RedisClusterUtilsHolder.INSTANCE;
	}
	
	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}
	
	/**
	 * 读取keys
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern) {
		Set<String> keys = new HashSet<String>();
		Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
		for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
			Jedis connection = entry.getValue().getResource();
			try {
				Set<byte[]> keysByte = connection.keys(SafeEncoder.encode(pattern));
				for (byte[] keyByte : keysByte) {
					try {
						keys.add((String) SerializerUtils.read(keyByte));
					} catch (Exception e) {
						keys.add(SafeEncoder.encode(keyByte));
					}
				}
			} finally {
				connection.close();
			}
		}
		return keys;
	}
	
	/**
	 * 返回key的类型
	 * The type can be one of "none", string", "list", "set", "zset", "hash"
	 * @param key
	 * @return
	 */
	public String type(String key) {
		try {
			return jedisCluster.type(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return "none";
	}
	
	/**
	 * 是否存在key
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		try {
			return jedisCluster.exists(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 设置过期时间
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Long expire(String key, int seconds) {
		try {
			return jedisCluster.expire(SerializerUtils.write(key), seconds);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0L;
	}
	
	/**
	 * 缓存数据
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		try {
			jedisCluster.set(SerializerUtils.write(key), SerializerUtils.write(value));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * 缓存数据
	 * @param key
	 * @param value
	 */
	public void set(Object key, Object value) {
		try {
			jedisCluster.set(SerializerUtils.write(key), SerializerUtils.write(value));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 缓存数据并设置过期时间
	 * @param key
	 * @param value
	 * @param expireTime
	 */
	public void set(String key, Object value, int expireTime) {
		try {
			byte[] cache_key = SerializerUtils.write(key);
			jedisCluster.set(cache_key, SerializerUtils.write(value));
			jedisCluster.expire(cache_key, expireTime);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 缓存数据并设置过期时间
	 * @param key
	 * @param value
	 * @param expireTime
	 */
	public void set(Object key, Object value, int expireTime) {
		try {
			byte[] cache_key = SerializerUtils.write(key);
			jedisCluster.set(cache_key, SerializerUtils.write(value));
			jedisCluster.expire(cache_key, expireTime);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 缓存数据
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setnx(Object key, Object value) {
		try {
			return jedisCluster.setnx(SerializerUtils.write(key), SerializerUtils.write(value));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0L;
	}
	
	/**
	 * 递增KEY
	 * @param key
	 * @return
	 */
	public Long incr(Object key) {
		try {
			return jedisCluster.incr(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0L;
	}
	
	/**
	 * 递增KEY
	 * @param key
	 * @param incr
	 * @return
	 */
	public Long incrBy(Object key, long incr) {
		try {
			return jedisCluster.incrBy(SerializerUtils.write(key), incr);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0L;
	}
	
	/**
	 * 递减KEY
	 * @param key
	 * @return
	 */
	public Long decr(Object key) {
		try {
			return jedisCluster.decr(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0L;
	}
	
	/**
	 * 递减KEY
	 * @param key
	 * @param decr
	 * @return
	 */
	public Long decrBy(Object key, long decr) {
		try {
			return jedisCluster.decrBy(SerializerUtils.write(key), decr);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0L;
	}
	
	/**
	 * 读取数据
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		if (StringUtils.isBlank(key)) return null;
		try {
			byte[] value = jedisCluster.get(SerializerUtils.write(key));
			if (null != value && value.length != 0) {
				return SerializerUtils.read(value);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 读取数据
	 * @param key
	 * @return
	 */
	public Object get(Object key) {
		if (null == key) return null;
		try {
			byte[] value = jedisCluster.get(SerializerUtils.write(key));
			if (null != value && value.length != 0) {
				return SerializerUtils.read(value);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 读取数据
	 * @param key
	 * @param value
	 * @return
	 */
	public Object getSet(Object key, Object value) {
		if (null == key || null == value) return null;
		try {
			byte[] ovalue = jedisCluster.getSet(SerializerUtils.write(key), SerializerUtils.write(value));
			if (null != ovalue && ovalue.length != 0) {
				return SerializerUtils.read(ovalue);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 刪除KEY
	 * @param key
	 * @return
	 */
	public Long delete(String key) {
		if (StringUtils.isBlank(key)) return null;
		try {
			return jedisCluster.del(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 刪除KEY
	 * @param key
	 * @return
	 */
	public Long delete(Object key) {
		if (null == key) return null;
		try {
			return jedisCluster.del(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 新增SET数据集合
	 * @param key
	 * @param members
	 * @return
	 */
	public long sadd(String key, String... members) {
		try {
			return jedisCluster.sadd(key, members);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * 新增SET数据集合
	 * @param key
	 * @param members
	 * @return
	 */
	public long sadd(String key, Object... members) {
		try {
			if (null == members|| members.length == 0) return 0;
			byte[][] bmembers = new byte[members.length][];
			for (int i = 0, len = members.length; i < len; i++) {
				bmembers[i] = SerializerUtils.write(members[i]);
			}
			return jedisCluster.sadd(SerializerUtils.write(key), bmembers);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * SET数据集合成员是否存在
	 * @param key
	 * @param member
	 * @return
	 */
	public boolean sismember(String key, String member) {
		try {
			return jedisCluster.sismember(key, member);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * SET数据集合长度
	 * @param key
	 * @return
	 */
	public long scard(String key) {
		try {
			return jedisCluster.scard(key);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * 获取KEY中SET数据集合
	 * @param key
	 * @return
	 */
	public Set<String> smembers(String key) {
		try {
			return jedisCluster.smembers(key);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 获取KEY中SET数据集合
	 * @param key
	 * @return
	 */
	public Set<Object> smembers2obj(String key) {
		Set<Object> results = new HashSet<Object>();
		try {
			Set<byte[]> bresults = jedisCluster.smembers(SerializerUtils.write(key));
			if (null == bresults || bresults.isEmpty()) return results;
			for (byte[] bresult : bresults) {
				results.add(SerializerUtils.read(bresult));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return results;
	}
	
	/**
	 * 删除SET数据集合
	 * @param key
	 * @param members
	 * @return
	 */
	public long srem(String key, String... members) {
		try {
			return jedisCluster.srem(key, members);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * 删除SET数据集合
	 * @param key
	 * @param members
	 * @return
	 */
	public long srem(String key, Object... members) {
		try {
			if (null == members|| members.length == 0) return 0;
			byte[][] bmembers = new byte[members.length][];
			for (int i = 0, len = members.length; i < len; i++) {
				bmembers[i] = SerializerUtils.write(members[i]);
			}
			return jedisCluster.srem(SerializerUtils.write(key), bmembers);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * 队列插入数据
	 * @param key
	 * @param value
	 * @return
	 */
	public long listPush(String key, Object value) {
		try {
			return jedisCluster.lpush(SerializerUtils.write(key), SerializerUtils.write(value));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * 队列取出数据
	 * @param key
	 * @return
	 */
	public Object listPop(String key) {
		try {
			byte[] returnObject = jedisCluster.rpop(SerializerUtils.write(key));
			if (null != returnObject && returnObject.length != 0) {
				return SerializerUtils.read(returnObject);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 队列数据长度
	 * @param key
	 * @return
	 */
	public long listLength(String key) {
		try {
			return jedisCluster.llen(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * Hash缓存数据
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hset(String key, String field, Object value) {
		try {
			return jedisCluster.hset(SerializerUtils.write(key), 
				SerializerUtils.write(field), SerializerUtils.write(value));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * Hash缓存数据
	 * @param key
	 * @param map
	 * @return
	 */
	public String hset(String key, Map<String, ?> value) {
		try {
			Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>();
			for (Map.Entry<String, ?> entry : value.entrySet()) {
				hash.put(SerializerUtils.write(entry.getKey()), 
					SerializerUtils.write(entry.getValue()));
			}
			return jedisCluster.hmset(SerializerUtils.write(key), hash);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Hash缓存实体Bean
	 * @param key
	 * @param value
	 * @param expireTime
	 * @return
	 */
	public String hsetBean(Object key, Object value, int expireTime) {
		try {
			if (null == value) return null;
			Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>();
			Map<String, Object> map = ReflectUtils.convertObjectToObjectMap(value);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				hash.put(SerializerUtils.write(entry.getKey()), 
					SerializerUtils.write(entry.getValue()));
			}
			byte[] keyBytes = SerializerUtils.write(key);
			String result = jedisCluster.hmset(keyBytes, hash);
			jedisCluster.expire(keyBytes, expireTime);
			return result;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Hash缓存实体Bean
	 * @param key
	 * @param value
	 * @return
	 */
	public String hsetBean(Object key, Object value) {
		try {
			if (null == value) return null;
			Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>();
			Map<String, Object> map = ReflectUtils.convertObjectToObjectMap(value);
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				hash.put(SerializerUtils.write(entry.getKey()), 
					SerializerUtils.write(entry.getValue()));
			}
			return jedisCluster.hmset(SerializerUtils.write(key), hash);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Hash是否存在field
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hexists(String key, String field) {
		try {
			return jedisCluster.hexists(SerializerUtils.write(key), SerializerUtils.write(field));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Hash字段自增数据
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public long hincrBy(String key, String field, long value) {
		try {
			return jedisCluster.hincrBy(SerializerUtils.write(key), 
				SerializerUtils.write(field), value);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * Hash读取数据
	 * @param key
	 * @param field
	 * @return
	 */
	public Object hget(String key, String field) {
		try {
			byte[] returnObject = jedisCluster.hget(SerializerUtils.write(key), SerializerUtils.write(field));
			if (null != returnObject && returnObject.length != 0) {
				return SerializerUtils.read(returnObject);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Object hgetBean(String key, Class<?> entityClass) {
		try {
			Map<byte[], byte[]> hash = jedisCluster.hgetAll(SerializerUtils.write(key));
			if (null != hash && !hash.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (Map.Entry<byte[], byte[]> entry : hash.entrySet()) {
					map.put((String) SerializerUtils.read(entry.getKey()), 
						SerializerUtils.read(entry.getValue()));
				}
				Object returnObject = entityClass.newInstance();
				ReflectUtils.convertObjectMapToObject(map, returnObject);
				return returnObject;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Hash读取数据
	 * @param key
	 * @return
	 */
	public Map<String, Object> hgetAll(String key) {
		try {
			Map<byte[], byte[]> returnObject = jedisCluster.hgetAll(SerializerUtils.write(key));
			if (null != returnObject && !returnObject.isEmpty()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (Map.Entry<byte[], byte[]> entry : returnObject.entrySet()) {
					map.put((String) SerializerUtils.read(entry.getKey()), 
						SerializerUtils.read(entry.getValue()));
				}
				return map;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Hash刪除fields
	 * @param key
	 * @param fields
	 * @return
	 */
	public long hdel(String key, String... fields) {
		try {
			return jedisCluster.hdel(SerializerUtils.write(key), SerializerUtils.write(fields));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * SortedSet新增成员
	 * @param key
	 * @param member
	 * @param score
	 * @return
	 */
	public long zadd(String key, Object member, double score) {
		try {
			return jedisCluster.zadd(SerializerUtils.write(key), score, SerializerUtils.write(member));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * SortedSet新增成员
	 * @param key
	 * @param member
	 * @param score
	 * @return
	 */
	public long zadd(String key, Map<Object, Double> members) {
		try {
			Map<byte[], Double> scoreMembers = new HashMap<byte[], Double>();
			for (Map.Entry<Object, Double> entry : members.entrySet()) {
				scoreMembers.put(SerializerUtils.write(entry.getKey()), entry.getValue());
			}
			return jedisCluster.zadd(SerializerUtils.write(key), scoreMembers);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * SortedSet读取成员
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<Object> zrange(String key, long start, long end) {
		Set<Object> results = new HashSet<Object>();
		try {
			Set<byte[]> bresults = jedisCluster.zrange(SerializerUtils.write(key), start, end);
			if (null == bresults || bresults.isEmpty()) return results;
			for (byte[] bresult : bresults) {
				results.add(SerializerUtils.read(bresult));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return results;
	}
	
	/**
	 * SortedSet读取成员和分数
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Map.Entry<String, Double>> zrangeWithScores(String key, long start, long end) {
		List<Map.Entry<String, Double>> resultList = new ArrayList<Map.Entry<String, Double>>();
		try {
			Set<Tuple> tuples = jedisCluster.zrangeWithScores(SerializerUtils.write(key), start, end);
			if (null == tuples || tuples.isEmpty()) return resultList;
			Map<String, Double> results = new HashMap<String, Double>();
			for (Tuple tuple : tuples) {
				results.put(String.valueOf(SerializerUtils.read(tuple.getBinaryElement())), tuple.getScore());
			}
			resultList.addAll(results.entrySet());
			Collections.sort(resultList, new Comparator<Map.Entry<String, Double>>(){
				@Override
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return resultList;
	}
	
	/**
	 * SortedSet读取成员
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<Object> zrevrange(String key, long start, long end) {
		Set<Object> results = new HashSet<Object>();
		try {
			Set<byte[]> bresults = jedisCluster.zrevrange(SerializerUtils.write(key), start, end);
			if (null == bresults || bresults.isEmpty()) return results;
			for (byte[] bresult : bresults) {
				results.add(SerializerUtils.read(bresult));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return results;
	}
	
	/**
	 * SortedSet读取成员和分数
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Map.Entry<String, Double>> zrevrangeWithScores(String key, long start, long end) {
		List<Map.Entry<String, Double>> resultList = new ArrayList<Map.Entry<String, Double>>();
		try {
			Set<Tuple> tuples = jedisCluster.zrevrangeWithScores(SerializerUtils.write(key), start, end);
			if (null == tuples || tuples.isEmpty()) return resultList;
			Map<String, Double> results = new HashMap<String, Double>();
			for (Tuple tuple : tuples) {
				results.put(String.valueOf(SerializerUtils.read(tuple.getBinaryElement())), tuple.getScore());
			}
			resultList.addAll(results.entrySet());
			Collections.sort(resultList, new Comparator<Map.Entry<String, Double>>(){
				@Override
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					return -o1.getValue().compareTo(o2.getValue());
				}
			});
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return resultList;
	}
	
	/**
	 * SORTEDSET数据集合长度
	 * @param key
	 * @return
	 */
	public long zcard(String key) {
		try {
			return jedisCluster.zcard(SerializerUtils.write(key));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * SortedSet移除成员
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrem(String key, Object... members) {
		try {
			if (null == members|| members.length == 0) return 0;
			byte[][] bmembers = new byte[members.length][];
			for (int i = 0, len = members.length; i < len; i++) {
				bmembers[i] = SerializerUtils.write(members[i]);
			}
			return jedisCluster.zrem(SerializerUtils.write(key), bmembers);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
}
