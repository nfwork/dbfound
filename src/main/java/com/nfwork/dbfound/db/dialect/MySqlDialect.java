package com.nfwork.dbfound.db.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySqlDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		String pagerSql = sql + " limit " + startWith + " , " + pagerSize;
		return pagerSql;
	}

	@Override
	public String getWhenSql(String when) {
		String whenSql = "select " + when;
		return whenSql;
	}

}
