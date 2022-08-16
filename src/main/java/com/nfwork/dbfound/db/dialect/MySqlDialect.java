package com.nfwork.dbfound.db.dialect;

public class MySqlDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		String pagerSql = sql + " limit " + startWith + " , " + pagerSize;
		return pagerSql;
	}

	@Override
	public String getWhenSql(String when) {
		String whenSql = "select " + when;
		return whenSql;
	}

}
