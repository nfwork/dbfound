package com.nfwork.dbfound.db.dialect;

public class InformixDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		int index = sql.toLowerCase().indexOf("select") + 6;
		String pagerSql = sql.substring(0, index) + " skip " + startWith
				+ " first " + pagerSize + sql.substring(index);
		return pagerSql;
	}

	@Override
	public String getWhenSql(String when) {
		String whenSql = "select 1 from dual where " + when;
		return whenSql;
	}

}
