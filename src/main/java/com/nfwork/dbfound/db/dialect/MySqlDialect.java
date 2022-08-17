package com.nfwork.dbfound.db.dialect;

public class MySqlDialect extends AbstractSqlDialect {

	@Override
	public String getPagerSql(String sql, String limitHold, String startHold) {
		return sql + " limit " + startHold +", "+limitHold;
	}

	@Override
	public String getWhenSql(String when) {
		return "select " + when;
	}

}
