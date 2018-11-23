package org.platform.utils.redis;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributedLock {
	
	private Logger LOG = LoggerFactory.getLogger(DistributedLock.class);
	
	private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
	
	/** Lock Key */
	private String lockKey = null;
	/** 锁超时时间，防止线程在入锁以后，无限的执行等待 */
    private int expireMillis = 60 * 1000;
    /** 锁等待时间，防止线程饥饿 */
    private int timeoutMillis = 10 * 1000;
    /** Lock Flag */
    private volatile boolean locked = false;
    
    private Random random = new Random();
    
    public DistributedLock(String lockKey) {
		this.lockKey = lockKey + "_lock";
	}
    
    public DistributedLock(String lockKey, int timeoutMillis) {
		this(lockKey);
		this.timeoutMillis = timeoutMillis;
	}
    
	public DistributedLock(String lockKey, int expireMillis, int timeoutMillis) {
		this(lockKey, timeoutMillis);
		this.expireMillis = expireMillis;
	}
    
	/**
     * 实现思路: 主要是使用了redis的setnx命令,缓存了锁.
     * reids缓存的key是锁的key,所有的共享value是锁的到期时间(注意:这里把过期时间放在value了,没有时间上设置其超时时间)
     * 执行过程:
     * 1.通过setnx尝试设置某个key的值,成功(当前没有这个锁)则返回,成功获得锁
     * 2.锁已经存在则获取锁的到期时间,和当前时间比较,超时的话,则设置新的值
     * @return true if lock is acquired, false acquire timeouted
     * @throws InterruptedException in case of thread interruption
     */
    public boolean lock() {
    	try {
	        int timeout = timeoutMillis;
	        while (timeout >= 0) {
	            long expireTime = System.currentTimeMillis() + expireMillis + 1;
	            if (RedisClusterUtils.getInstance().setnx(lockKey, expireTime) == 1) {
	                // lock acquired
	                locked = true;
	                return true;
	            }
	            Long lockTime = (Long) RedisClusterUtils.getInstance().get(lockKey); 
	            if (lockTime != null && lockTime < System.currentTimeMillis()) {
	                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
	                // lock is expired
	                Long oldTime = (Long) RedisClusterUtils.getInstance().getSet(lockKey, expireTime);
	                //获取上一个锁到期时间，并设置现在的锁到期时间，
	                //只有一个线程才能获取上一个线上的设置时间，因为jedis.getSet是同步的
	                if (oldTime != null && oldTime.equals(lockTime)) {
	                    //防止误删（覆盖，因为key是相同的）他人的锁——这里达不到效果，这里值会被覆盖，但是因为相差了很少的时间，所以可以接受
	
	                    //[分布式的情况下]:如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
	                    // lock acquired
	                    locked = true;
	                    return true;
	                }
	            }
	            timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;
	            /* 延迟100毫秒,这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即当同时到达多个进程,
	                            只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进程,也以同样的频率申请锁,
	                            这将可能导致前面来的锁得不到满足.使用随机的等待时间可以一定程度上保证公平性
	             */
	            Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS, random.nextInt(30));
	        }
    	} catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	    	return false;
	    }
        return false;
    }
    
    /**
     * 释放锁
     */
    public void unlock() {
    	if (locked) {
    		RedisClusterUtils.getInstance().delete(lockKey);
    		locked = false;
    	}
    }
    
    
}
