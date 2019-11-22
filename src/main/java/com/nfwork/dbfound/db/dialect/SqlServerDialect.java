package com.nfwork.dbfound.db.dialect;

public class SqlServerDialect implements SqlDialect {
	public String getPagerSql(String sql, int pagerSize, long startWith) {

		int i = sql.toLowerCase().lastIndexOf("order by");
		if (i == -1) {
			i = sql.toLowerCase().lastIndexOf("ORDER BY");
		}
		String esql = i == -1 ? sql : sql.substring(0, i);
		String orderBy = i == -1 ? "getdate()" : sql.substring(i + 9);

		int index = esql.toLowerCase().indexOf("select");
		esql = esql.substring(0, index + 6) + " row_number() over(order by " + orderBy + ") d_p_rm ," + esql.substring(index + 6);

		String pagersql = " select * from (" + esql + ") v where d_p_rm <=" + (startWith + pagerSize) + " and d_p_rm >= " + (startWith + 1);
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
