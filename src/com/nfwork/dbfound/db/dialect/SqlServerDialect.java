package com.nfwork.dbfound.db.dialect;

public class SqlServerDialect implements SqlDialect {
	public String getPagerSql(String sql, int pagerSize, long startWith) {

		int index = sql.toLowerCase().indexOf("select");
		sql = sql.substring(0, index+6)+" row_number()over(order by getdate()) d_p_rm ,"+sql.substring(index+6);
		
		String pagersql = " select * from (" + sql
		+ ") v where d_p_rm <=" + (startWith + pagerSize)
		+ " and d_p_rm >= " + (startWith + 1);
		
		return pagersql;
	}

	public String getWhenSql(String when) {
		String pagersql = "select " + when;
		return pagersql;
	}

	public String parseSql(String sql) {
		return sql;
	}
}
