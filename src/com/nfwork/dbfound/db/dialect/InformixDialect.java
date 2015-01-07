package com.nfwork.dbfound.db.dialect;

public class InformixDialect implements SqlDialect {

	public String getPagerSql(String sql, int pagerSize, long startWith) {
		int index = sql.toLowerCase().indexOf("select") + 6;
		String pagersql = sql.substring(0, index) + " skip " + startWith
				+ " first " + pagerSize + sql.substring(index);
		return pagersql;
	}

	public String getWhenSql(String when) {
		String pagersql = "select 1 from dual where " + when;
		return pagersql;
	}

	public String parseSql(String sql) {
		return sql;
	}

}
