package org.platform.utils.jdbc;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstrResultSetHandler implements ResultSetHandler{
	
	protected Logger LOG = LoggerFactory.getLogger(getClass());
	
	protected Class<?> clazz = null;
	
	protected void preHandle() {
		if (null == clazz) {
			LOG.error("clazz is null");
			throw new RuntimeException("clazz is null");
		}
	}
	
	protected void postHandle() {
		
	}
	
	protected abstract Object doHandle(ResultSet resultSet);

	@Override
	public Object handle(ResultSet resultSet) {
		preHandle();
		Object result = doHandle(resultSet);
		postHandle();
		return result;
	}

}
