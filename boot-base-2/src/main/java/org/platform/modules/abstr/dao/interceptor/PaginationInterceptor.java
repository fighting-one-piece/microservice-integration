package org.platform.modules.abstr.dao.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.platform.modules.abstr.dao.pagination.Dialect;
import org.platform.modules.abstr.dao.pagination.MySQL5Dialect;
import org.platform.modules.abstr.entity.Query;
import org.platform.utils.reflect.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationInterceptor implements Interceptor {

	private final static Logger LOG = LoggerFactory.getLogger(PaginationInterceptor.class);

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//Mybatis在进行Sql语句处理的时候都是建立的RoutingStatementHandler，而在RoutingStatementHandler里面拥有一个StatementHandler类型的delegate属性
		//RoutingStatementHandler会依据Statement的不同建立对应的BaseStatementHandler，即SimpleStatementHandler、PreparedStatementHandler或CallableStatementHandler
		//在RoutingStatementHandler里面所有StatementHandler接口方法的实现都是调用的delegate对应的方法。
	    //我们在PageInterceptor类上已经用@Signature标记了该Interceptor只拦截StatementHandler接口的prepare方法，又因为Mybatis只有在建立RoutingStatementHandler的时候
	    //是通过Interceptor的plugin方法进行包裹的，所以我们这里拦截到的目标对象肯定是RoutingStatementHandler对象
		RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate = (StatementHandler) ReflectUtils.getValueByFieldName(handler, "delegate");
	    BoundSql boundSql = delegate.getBoundSql();
	    //获取当前要执行的Sql语句，也就是我们直接在Mapper映射语句中写的Sql语句
        String sql = boundSql.getSql();
        String judgeSQL = sql.toUpperCase().trim();
	    if(judgeSQL.startsWith("INSERT") || judgeSQL.startsWith("UPDATE") || judgeSQL.startsWith("DELETE")) {
	    	return invocation.proceed();
	    }
	    Object offsetObj = null, limitObj = null;
	    Object parameterObject = boundSql.getParameterObject();
	    if (parameterObject instanceof Query) {
	    	Query query = (Query) parameterObject;
	    	if (!query.isPagination()) return invocation.proceed();
	    	offsetObj = query.getCondition().get(Query.OFFSET);
	    	limitObj = query.getCondition().get(Query.LIMIT);
	    } else if (parameterObject instanceof Map) {
	    	Map<String, Object> parameters = (Map<String, Object>) parameterObject;
	    	if (null != parameters && !parameters.isEmpty()) {
	    		if (!parameters.containsKey(Query.IS_PAGINATION)) return invocation.proceed();
	 	    	Object paginationParam = parameters.get(Query.IS_PAGINATION);
	 	    	if (null == paginationParam || !((Boolean) paginationParam)) return invocation.proceed();
	    	}
	    	offsetObj = parameters.get(Query.OFFSET);
	    	limitObj = parameters.get(Query.LIMIT);
	    }
	    if (null == offsetObj || null == limitObj) throw new RuntimeException("offset or limit is null");
	    //获取MyBatis配置信息
        Configuration configuration = (Configuration) ReflectUtils.getValueByFieldName(delegate, "configuration");
        String dialectValue = configuration.getVariables().getProperty("dialect");
        if (null == dialectValue) throw new RuntimeException("the value of the dialect is null");
        Dialect.Type dialectType = Dialect.Type.valueOf(dialectValue.toUpperCase());;
		Dialect dialect = getDialect(dialectType);
    	LOG.debug("dialectType: {} offset: {} limit: {}", dialectType, offsetObj, limitObj);
        String paginationSql = dialect.obtainPageSql(sql, (Integer) offsetObj, (Integer) limitObj);
        //利用反射设置当前BoundSql对应的sql属性为我们建立好的分页Sql语句
        ReflectUtils.setValueByFieldName(boundSql, "sql", paginationSql);
        LOG.debug("生成分页SQL : {}", boundSql.getSql());
        MappedStatement mappedStatement = (MappedStatement) ReflectUtils.getValueByFieldName(delegate, "mappedStatement");
        //拦截到的prepare方法参数是一个Connection对象
        Connection connection = (Connection) invocation.getArgs()[0];
        if (parameterObject instanceof Query) {
        	Query query = (Query) parameterObject;
        	Long totalRowNum = execCountQuery(connection, mappedStatement, query);
        	query.setTotalRowNum(totalRowNum);
        } else if (parameterObject instanceof Map) {
        	Map<String, Object> parameters = (Map<String, Object>) parameterObject;
        	Long totalRowNum = execCountQuery(connection, mappedStatement, parameters);
        	if (null != totalRowNum) parameters.put(Query.TOTAL_ROW_NUM, totalRowNum);
        }
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}
	
	/**
	 * 根据类型获取方言
	 * @param dialectType
	 * @return
	 */
	private Dialect getDialect(Dialect.Type dialectType) {
		Dialect dialect = null;
		switch (dialectType) {
		case MYSQL:
			dialect = new MySQL5Dialect(); break;
		case MSSQL:
			break;
		case ORACLE:
			break;
		default:
			dialect = new MySQL5Dialect();
		}
		return dialect;
	}

	/**
     * 总记录数查询
     * @param connection 当前的数据库连接
     * @param mappedStatement Mapper映射语句
     * @param parameters Mapper映射语句对应的参数对象
     */
    @SuppressWarnings("unchecked")
	private Long execCountQuery(Connection connection, MappedStatement mappedStatement, Object parameters) {
       BoundSql boundSql = mappedStatement.getBoundSql(parameters);
       //通过查询Sql语句获取到对应的计算总记录数的sql语句
       String countSql = obtainCountSql(boundSql.getSql());
       if (null == countSql)  return null;
       //通过BoundSql获取对应的参数映射
       List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
       //利用Configuration、查询记录数的Sql语句countSql、参数映射关系parameterMappings和参数对象page建立查询记录数对应的BoundSql对象。
       BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, parameters);
       Map<String, Object> additionalParameters = (Map<String, Object>) ReflectUtils.getValueByFieldName(boundSql, "additionalParameters");
       for (Map.Entry<String, Object> additionalParameter : additionalParameters.entrySet()) {
    	   countBoundSql.setAdditionalParameter(additionalParameter.getKey(), additionalParameter.getValue());
       }
       //通过mappedStatement、参数对象page和BoundSql对象countBoundSql建立一个用于设定参数的ParameterHandler对象
       ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameters, countBoundSql);
       //通过connection建立一个countSql对应的PreparedStatement对象。
       PreparedStatement pstmt = null;
       ResultSet rs = null;
       Long totalRowNum = 0L;
       try {
           pstmt = connection.prepareStatement(countSql);
           parameterHandler.setParameters(pstmt);
           rs = pstmt.executeQuery();
           if (rs.next()) totalRowNum = rs.getLong(1);
       } catch (SQLException e) {
           LOG.error(e.getMessage(), e);
       } finally {
           try {
              if (rs != null) rs.close();
              if (pstmt != null) pstmt.close();
           } catch (SQLException e) {
              LOG.error(e.getMessage(), e);
           }
       }
       return totalRowNum;
    }

    /**
     * 根据原Sql语句获取对应的查询总记录数的Sql语句
     * @param sql
     * @return
     */
    private String obtainCountSql(String sql) {
    	StringBuilder sb = new StringBuilder(100);
    	sb.append("SELECT COUNT(1) FROM (").append(sql).append(") as countSQL");
    	return sb.toString();
    }

}