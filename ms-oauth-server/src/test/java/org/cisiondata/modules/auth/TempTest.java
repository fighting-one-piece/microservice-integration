package org.cisiondata.modules.auth;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.cisiondata.utils.date.DateFormatter;
import org.cisiondata.utils.endecrypt.Base64Utils;
import org.cisiondata.utils.http.HttpClientUtils;
import org.cisiondata.utils.redis.ShardedJedisPoolWrapper;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class TempTest {
	
	public static void oauthToken() {
		String url = "http://192.168.0.121:16000/oauth/token";
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "password");
		params.put("username", "admin");
		params.put("password", "admin");
		String[] headers = new String[]{"Authorization", "Basic d2ViX2NsaWVudDp3ZWJfc2VjcmV0"};
		String response = HttpClientUtils.sendPost(url, params, "UTF-8", headers);
		System.err.println(response);
		System.err.println("end!!!");
	}
	
	public static void a() {
		System.err.println(Base64Utils.encode("web_client:web_secret"));
	}
	
	public static void b() {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(5);
		config.setMaxIdle(10);
		config.setMaxTotal(20);
		String address = "192.168.0.124:6379";
		ShardedJedisPoolWrapper shardedJedisPoolWrapper = new ShardedJedisPoolWrapper(config, address);
		ShardedJedisPool shardedJedisPool = shardedJedisPoolWrapper.getJedisPool();
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		shardedJedis.set("t:key:1", "t:value:1");
		System.err.println(shardedJedis.get("t:key:1"));
		shardedJedis.close();
	}
	
	public static void c() {
		long millis = 1520663667999L;
		Calendar calendar = Calendar.getInstance();
		System.err.println(calendar.getTimeInMillis());
		calendar.setTimeInMillis(millis);
		System.err.println(DateFormatter.TIME.get().format(calendar.getTime()));
	}

	public static void main(String[] args) {
		c();
	}
	
}
