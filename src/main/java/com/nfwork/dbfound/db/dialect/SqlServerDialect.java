package com.nfwork.dbfound.db.dialect;

public class SqlServerDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {

		int i = sql.toLowerCase().lastIndexOf("order by");
		String esql = i == -1 ? sql : sql.substring(0, i);
		String orderBy = i == -1 ? "getdate()" : sql.substring(i + 9);

		int index = esql.toLowerCase().indexOf("select");
		esql = esql.substring(0, index + 6) + " row_number() over(order by " + orderBy + ") d_p_rm ," + esql.substring(index + 6);

		return " select * from (" + esql + ") v where d_p_rm <=" + (startWith + pagerSize) + " and d_p_rm >= " + (startWith + 1);
	}

	@Override
	public String getWhenSql(String when) {
		return "select case when " + when + " then 1 else 0 end ";
	}

}
