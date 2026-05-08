package com.nfwork.dbfound.db.dialect;

public class ClickHouseDialect extends AbstractSqlDialect {

	@Override
	public String getPagerSql(String sql, String limitHold, String startHold) {
		return sql + " limit " + limitHold + " offset " + startHold;
	}

	@Override
	public String getWhenSql(String when) {
		return "select " + when;
	}

}
