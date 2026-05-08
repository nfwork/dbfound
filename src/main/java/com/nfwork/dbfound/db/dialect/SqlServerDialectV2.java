package com.nfwork.dbfound.db.dialect;

import com.nfwork.dbfound.util.StringUtil;

public class SqlServerDialectV2 extends AbstractSqlDialect {

	@Override
	public String getPagerSql(String sql, String limitHold, String startHold) {
		if (!StringUtil.containsIgnoreCase(sql, "order by")) {
			sql = sql + " order by (select null)";
		}
		return sql + " offset " + startHold + " rows fetch next " + limitHold + " rows only";
	}

	@Override
	public String getWhenSql(String when) {
		return "select case when " + when + " then 1 else 0 end ";
	}

}
