package org.platform.modules.cache.controller;

import java.util.Map;

import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.serde.SerializerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {
	
	private Logger LOG = LoggerFactory.getLogger(CacheController.class);
	
	/**
	 * 返回key的类型
	 * The type can be one of "none", string", "list", "set", "zset", "hash"
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/type", method = RequestMethod.GET)
	public String type(@RequestParam String key) {
		try {
			return RedisClusterUtils.getInstance().type(key);
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
	@RequestMapping(value = "/exists", method = RequestMethod.GET)
	public boolean exists(@RequestParam String key) { 
		try {
			return RedisClusterUtils.getInstance().exists(key);
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
	@RequestMapping(value = "/expire", method = RequestMethod.GET)
	public Long expire(@RequestParam String key, @RequestParam int seconds) {
		try {
			return RedisClusterUtils.getInstance().expire(key, seconds);
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
	@RequestMapping(value = "/set", method = RequestMethod.POST)
	public int set(@RequestParam Object key, @RequestParam Object value) {
		try {
			RedisClusterUtils.getInstance().set(SerializerUtils.write(key), SerializerUtils.write(value));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
		return 1;
	}

	/**
	 * 缓存数据并设置过期时间
	 * @param key
	 * @param value
	 * @param expireTime
	 */
	@RequestMapping(value = "/set/expiretime", method = RequestMethod.POST)
	public int set(@RequestParam Object key, @RequestParam Object value, @RequestParam int expireTime) {
		try {
			RedisClusterUtils.getInstance().set(key, value, expireTime);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
		return 1;
	}
	
	/**
	 * 缓存数据
	 * @param key
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/setnx", method = RequestMethod.POST)
	public Long setnx(@RequestParam Object key, @RequestParam Object value) {
		try {
			return RedisClusterUtils.getInstance().setnx(key, value);
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
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Object get(@RequestParam Object key) {
		try {
			return RedisClusterUtils.getInstance().get(key);
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
	@RequestMapping(value = "/getset", method = RequestMethod.GET)
	public Object getSet(@RequestParam Object key, @RequestParam Object value) {
		try {
			return RedisClusterUtils.getInstance().getSet(key, value);
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
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Long delete(@RequestParam Object key) {
		try {
			return RedisClusterUtils.getInstance().delete(key);
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
	@RequestMapping(value = "/sadd", method = RequestMethod.POST)
	public long sadd(@RequestParam String key, @RequestParam String... members) {
		try {
			return RedisClusterUtils.getInstance().sadd(key, members);
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
	@RequestMapping(value = "/srem", method = RequestMethod.POST)
	public long srem(@RequestParam String key, @RequestParam String... members) {
		try {
			return RedisClusterUtils.getInstance().srem(key, members);
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
	@RequestMapping(value = "/sismember", method = RequestMethod.GET)
	public boolean sismember(@RequestParam String key, @RequestParam String member) {
		try {
			return RedisClusterUtils.getInstance().sismember(key, member);
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
	@RequestMapping(value = "/scard", method = RequestMethod.GET)
	public long scard(@RequestParam String key) {
		try {
			return RedisClusterUtils.getInstance().scard(key);
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
	@RequestMapping(value = "/lpush", method = RequestMethod.POST)
	public long lpush(@RequestParam String key, @RequestParam Object value) {
		try {
			return RedisClusterUtils.getInstance().listPush(key, value);
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
	@RequestMapping(value = "/rpop", method = RequestMethod.GET)
	public Object rpop(@RequestParam String key) {
		try {
			return RedisClusterUtils.getInstance().listPop(key);
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
	@RequestMapping(value = "/llen", method = RequestMethod.GET)
	public long listLength(@RequestParam String key) {
		try {
			return RedisClusterUtils.getInstance().listLength(key);
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
	@RequestMapping(value = "/hset", method = RequestMethod.POST)
	public long hset(@RequestParam String key, @RequestParam String field, @RequestParam Object value) {
		try {
			return RedisClusterUtils.getInstance().hset(key, field, value);
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
	@RequestMapping(value = "/hmset", method = RequestMethod.POST)
	public String hset(@RequestParam String key, @RequestParam Map<String, ?> value) {
		try {
			return RedisClusterUtils.getInstance().hset(key, value);
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
	@RequestMapping(value = "/hset/bean", method = RequestMethod.POST)
	public String hsetBean(@RequestParam Object key, @RequestParam Object value) {
		try {
			return RedisClusterUtils.getInstance().hsetBean(key, value);
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
	@RequestMapping(value = "/hset/bean/expiretime", method = RequestMethod.POST)
	public String hsetBean(@RequestParam Object key, @RequestParam Object value, @RequestParam int expireTime) {
		try {
			return RedisClusterUtils.getInstance().hsetBean(key, value, expireTime);
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
	@RequestMapping(value = "/hexists", method = RequestMethod.GET)
	public boolean hexists(@RequestParam String key, @RequestParam String field) {
		try {
			return RedisClusterUtils.getInstance().hexists(key, field);
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
	@RequestMapping(value = "/hincrby", method = RequestMethod.POST)
	public long hincrBy(@RequestParam String key, @RequestParam String field, @RequestParam long value) {
		try {
			return RedisClusterUtils.getInstance().hincrBy(key, field, value);
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
	@RequestMapping(value = "/hget", method = RequestMethod.GET)
	public Object hget(@RequestParam String key, @RequestParam String field) {
		try {
			return RedisClusterUtils.getInstance().hget(key, field);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	@RequestMapping(value = "/hget/bean", method = RequestMethod.GET)
	public Object hgetBean(@RequestParam String key, @RequestParam Class<?> entityClass) {
		try {
			return RedisClusterUtils.getInstance().hgetBean(key, entityClass);
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
	@RequestMapping(value = "/hgetall", method = RequestMethod.GET)
	public Map<String, Object> hgetAll(@RequestParam String key) {
		try {
			return RedisClusterUtils.getInstance().hgetAll(key);
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
	@RequestMapping(value = "/hdel", method = RequestMethod.POST)
	public long hdel(@RequestParam String key, @RequestParam String... fields) {
		try {
			return RedisClusterUtils.getInstance().hdel(key, fields);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return 0;
	}
	
}
