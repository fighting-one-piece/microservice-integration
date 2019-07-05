package org.platform.utils.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JDBC工具类*/
public class JDBCUtils {
	
	private static Logger LOG = LoggerFactory.getLogger(JDBCUtils.class);
	
	private static final String CONFIG_FILE = "application-%s.properties";
	
	private static ConnectionPool connectionPool = null;
	
	static{
		InputStream in = null;
		try {
			String activeEnv = System.getProperty("spring.profiles.active");
			activeEnv = StringUtils.isBlank(activeEnv) ? "development" : activeEnv;
			in = JDBCUtils.class.getClassLoader().getResourceAsStream(String.format(CONFIG_FILE, activeEnv));
			Properties properties = new Properties();
			properties.load(in);
			String driverClassName = properties.getProperty("spring.datasource.jdbc.driverClassName");
			String jdbcUrl = properties.getProperty("spring.datasource.jdbc.url");
			String username = properties.getProperty("spring.datasource.jdbc.username");
			String password = properties.getProperty("spring.datasource.jdbc.password");
			int initialSize = (int) properties.get("spring.datasource.jdbc.initialSize");
			int maxActive = (int) properties.get("spring.datasource.jdbc.maxActive");
			connectionPool = new ConnectionPool(driverClassName, jdbcUrl, 
				username, password, initialSize, maxActive);
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		} finally {
			try {
				if (null != in) in.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	public static synchronized Connection obtainConnection() {
		Connection connection = null;
		try {
			connection = connectionPool.getConnection();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
		return connection;
	}
	
	/**
	 * 释放连接
	 * @param conn 连接
	 * @param st
	 * @param rs
	 */
	public static void release(Connection conn, Statement st, ResultSet rs) {
		try{
			if (null != rs) rs.close();
			if (null != st) st.close();
			if (null != conn) conn.close();
		}catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
	}
	
	/**
	 * 返回连接到连接池
	 * @param conn
	 */
	public static void returnConnection (Connection conn) {
		try{
			if (null != conn) connectionPool.returnConnection(conn);
		}catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
	}
	
	/**
	 * 执行无返回值语句
	 * @param sql 语句
	 * @param params 参数
	 * @return
	 */
	public static void execute(String sql, Object... params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			conn = obtainConnection();
			pstmt = conn.prepareStatement(sql);
			if(null != params){
				for(int i = 0; i < params.length; i++){
					pstmt.setObject(i+1, params[i]);
				}
			} 
			pstmt.execute();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		} finally {
			returnConnection(conn);
			release(null, pstmt, null);
		}
	}
	
	/**
	 * 执行有返回值语句
	 * @param sql 语句
	 * @param handler 返回值处理
	 * @param params 参数
	 * @return
	 */
	public static Object execute(String sql, ResultSetHandler handler, Object... params) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try{
			conn = obtainConnection();
			pstmt = conn.prepareStatement(sql);
			if(null != params){
				for(int i = 0; i < params.length; i++){
					pstmt.setObject(i+1, params[i]);
				}
			}
			return handler.handle(pstmt.executeQuery());
		} catch (SQLException e) {
			LOG.info(e.getMessage(), e);
		} finally{
			returnConnection(conn);
			release(null, pstmt, result);
		}
		return null;
	}
	
}
