package com.nfwork.dbfound.db.dialect;

public class AccessDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		return sql;
	}

	@Override
	public String getWhenSql(String when) {
		String whenSql = "select " + when;
		return whenSql;
	}
}
