package com.nfwork.dbfound.db.dialect;

public class OracleDialect extends AbstractSqlDialect {

	@Override
	public String getPagerSql(String sql, String limitHold, String startHold) {
		return "select * from (select v.*, rownum d_rm from (" + sql
				+ ") v where rownum <= " + startHold + " + " + limitHold
				+ ") where d_rm >= " + startHold + " + 1";
	}

	@Override
	public String getWhenSql(String when) {
		return "select 1 from dual where " + when;
	}

}
