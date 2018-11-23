package org.platform.utils.redis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DLockInterceptor implements InvocationHandler {

	private Object target = null;

	public DLockInterceptor(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		DLock lock = method.getAnnotation(DLock.class);
		if (null == lock) return method.invoke(this.target, args);
		DistributedLock distributedLock = new DistributedLock(lock.lockKey());
		try {
			boolean locked = distributedLock.lock();
			if (!locked) {
				return method.invoke(this.target, args);
			}
		} finally {
			distributedLock.unlock();
		}
		return null;
	}
	
}
