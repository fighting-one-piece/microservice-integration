package org.platform.modules.login.web.session;

import org.platform.utils.endecrypt.Base64Utils;
import org.platform.utils.endecrypt.IDUtils;
import org.platform.utils.redis.RedisClusterUtils;
import org.platform.utils.serde.SerializerUtils;
import org.springframework.stereotype.Component;

/**  基于REDIS缓存实现的会话处理器 **/
@Component("redisStorageHandler")
public class RedisStorageHandler extends StorageHandlerAdapter {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String createSessionId() throws SessionException {
		String sessionId = Base64Utils.encode(IDUtils.genUUID());
		RedisClusterUtils.getInstance().set(sessionKey(sessionId), "1", valueTTL);
		return sessionId;
	}

	@Override
	public boolean existsSessionId(String sessionId) throws SessionException {
		Object valueObj = RedisClusterUtils.getInstance().get(sessionKey(sessionId));
		return null == valueObj ? false : "1".equals((String) valueObj);
	}

	@Override
	public void initialize(String sessionId) throws SessionException {
		RedisClusterUtils.getInstance().set(sessionKey(sessionId), "1", valueTTL);
	}

	@Override
	public void invalidate(String sessionId) throws SessionException {
		RedisClusterUtils.getInstance().delete(sessionKey(sessionId));
		RedisClusterUtils.getInstance().delete(hashKey(sessionId));
	}

	@Override
	public void setAttribute(String sessionId, String name, Object value) throws SessionException {
		byte[] hashKey = hashKey(sessionId);
		if (value == null) {
			RedisClusterUtils.getInstance().getJedisCluster().hdel(hashKey, SerializerUtils.write(name));
		} else {
			RedisClusterUtils.getInstance().getJedisCluster().hset(hashKey, SerializerUtils.write(name), SerializerUtils.write(value));
		}
		RedisClusterUtils.getInstance().getJedisCluster().expire(hashKey, valueTTL);
	}

	@Override
	public Object getAttribute(String sessionId, String name) throws SessionException {
		return SerializerUtils.read(RedisClusterUtils.getInstance().getJedisCluster().hget(hashKey(sessionId), SerializerUtils.write(name)));
	}

	@Override
	public Object getAttributeAndRemove(String sessionId, String name) throws SessionException {
		byte[] flagHashKey = flagHashKey(sessionId, name);
		byte[] flag = RedisClusterUtils.getInstance().getJedisCluster().getSet(flagHashKey, SerializerUtils.write(System.currentTimeMillis()));
		if (null == flag || (System.currentTimeMillis() - (long) SerializerUtils.read(flag) > 5 * 60 * 1000)) {
			byte[] hashKey = hashKey(sessionId);
			byte[] nameByte = SerializerUtils.write(name);
			Object result = SerializerUtils.read(RedisClusterUtils.getInstance().getJedisCluster().hget(hashKey, nameByte));
			RedisClusterUtils.getInstance().getJedisCluster().hdel(hashKey, nameByte);
			RedisClusterUtils.getInstance().getJedisCluster().del(flagHashKey);
			return result;
		} 
		return null;
	}
	
	@Override
	public void removeAttribute(String sessionId, String name) throws SessionException {
		RedisClusterUtils.getInstance().getJedisCluster().hdel(hashKey(sessionId), SerializerUtils.write(name));
	}

}
