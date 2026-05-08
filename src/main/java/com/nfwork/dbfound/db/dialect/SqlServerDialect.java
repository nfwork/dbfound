package com.nfwork.dbfound.db.dialect;

public class SqlServerDialect extends AbstractSqlDialect {

	@Override
	public String getPagerSql(String sql, String limitHold, String startHold) {
		String holdSql = sql.toLowerCase();
		int orderIndex = holdSql.lastIndexOf("order by");
		String eSql = orderIndex == -1 ? sql : sql.substring(0, orderIndex);
		String orderBy = orderIndex == -1 ? "getdate()" : sql.substring(orderIndex + 8).trim();

		int selectIndex = holdSql.indexOf("select");
		eSql = eSql.substring(0, selectIndex + 6) + " row_number() over(order by " + orderBy + ") d_rm,"
				+ eSql.substring(selectIndex + 6);

		return "select * from (" + eSql + ") v where d_rm <= " + startHold + " + " + limitHold
				+ " and d_rm >= " + startHold + " + 1";
	}

	@Override
	public String getWhenSql(String when) {
		return "select case when " + when + " then 1 else 0 end ";
	}

}
