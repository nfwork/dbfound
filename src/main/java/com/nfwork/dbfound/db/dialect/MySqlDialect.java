package com.nfwork.dbfound.db.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySqlDialect implements SqlDialect {

	@Override
	public String getPagerSql(String sql, int pagerSize, long startWith) {
		String pagersql = sql + " limit " + startWith + " , " + pagerSize;
		return pagersql;
	}

	@Override
	public String getWhenSql(String when) {
		String pagersql = "select " + when;
		return pagersql;
	}

}
