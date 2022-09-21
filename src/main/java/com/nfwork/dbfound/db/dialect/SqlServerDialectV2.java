package com.nfwork.dbfound.db.dialect;

public class SqlServerDialectV2 extends AbstractSqlDialect {

	@Override
	public String getPagerSql(String sql, String limitHold, String startHold) {
		return sql + " offset "+ startHold +" rows fetch next "+limitHold+" rows only" ;
	}

	@Override
	public String getWhenSql(String when) {
		return "select case when " + when + " then 1 else 0 end ";
	}

}
