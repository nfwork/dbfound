package com.nfwork.dbfound.db.dialect;

public class OracleDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		String pagersql = "select * from (select v.*, rownum d_p_rm from (" + sql
				+ ") v where rownum<=" + (startWith + pagerSize)
				+ ") where d_p_rm >= " + (startWith + 1);
		return pagersql;
	}

	@Override
	public String getWhenSql(String when) {
		String pagersql = "select 1 from dual where " + when;
		return pagersql;
	}

}
