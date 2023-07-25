package com.nfwork.dbfound.db.dialect;

public class SqlServerDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {

		String holdSql = sql.toLowerCase();
		int orderIndex = holdSql.lastIndexOf("order by");
		String eSql = orderIndex == -1 ? sql : sql.substring(0, orderIndex);
		String orderBy = orderIndex == -1 ? "getdate()" : sql.substring(orderIndex + 9);

		int selectIndex = holdSql.indexOf("select");
		eSql = eSql.substring(0, selectIndex + 6) + " row_number() over(order by " + orderBy + ") d_rm ," + eSql.substring(selectIndex + 6);

		return " select * from (" + eSql + ") v where d_rm <=" + (startWith + pagerSize) + " and d_rm >= " + (startWith + 1);
	}

	@Override
	public String getWhenSql(String when) {
		return "select case when " + when + " then 1 else 0 end ";
	}

}
