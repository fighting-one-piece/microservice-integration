package org.platform.modules.abstr.dao.pagination;

public interface Dialect {

	public static enum Type {
        MYSQL {
            @SuppressWarnings("unused")
			public String getValue() {
                return "mysql";
            }
        },
        MSSQL {
            @SuppressWarnings("unused")
			public String getValue() {
                return "sqlserver";
            }
        },
        ORACLE {
            @SuppressWarnings("unused")
			public String getValue() {
                return "oracle";
            }
        }
    }

    /**
     * @descrption 获取分页SQL
     * @param sql 原始查询SQL
     * @param offset 开始记录索引（从零开始）
     * @param limit 每页记录大小
     * @return 返回数据库相关的分页SQL语句
     */
    public abstract String obtainPageSql(String sql, int offset, int limit);
}
