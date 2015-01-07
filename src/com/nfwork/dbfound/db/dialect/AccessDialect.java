package com.nfwork.dbfound.db.dialect;

public class AccessDialect implements SqlDialect {
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		return sql;
	}

	public String getWhenSql(String when) {
		String pagersql = "select " + when;
		return pagersql;
	}

	public String parseSql(String sql) {
		return sql;
	}
}
