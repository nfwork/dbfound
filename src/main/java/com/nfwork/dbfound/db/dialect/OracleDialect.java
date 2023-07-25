package com.nfwork.dbfound.db.dialect;

public class OracleDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		String pagerSql = "select * from (select v.*, rownum _drm from (" + sql
				+ ") v where rownum<=" + (startWith + pagerSize)
				+ ") where _drm >= " + (startWith + 1);
		return pagerSql;
	}

	@Override
	public String getWhenSql(String when) {
		String whenSql = "select 1 from dual where " + when;
		return whenSql;
	}

}
