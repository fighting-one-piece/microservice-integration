package org.platform.modules.abstr.dao.pagination;

public class MySQL5Dialect implements Dialect {

    @Override
	public String obtainPageSql(String sql, int offset, int limit) {
    	StringBuffer sb = new StringBuffer(sql);
    	sb.append(" limit ").append(offset).append(",").append(limit);
        return sb.toString();
    }

    public boolean supportsLimit() {
        return true;
    }

}
