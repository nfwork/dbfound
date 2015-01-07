package com.nfwork.dbfound.db.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySqlDialect implements SqlDialect {

	public String getPagerSql(String sql, int pagerSize, long startWith) {
		String pagersql = sql + " limit " + startWith + " , " + pagerSize;
		return pagersql;
	}

	public String getWhenSql(String when) {
		String pagersql = "select " + when;
		return pagersql;
	}

	public String parseSql(String sql) {
		/*sql = changSysDate(sql);
		sql = changNvl(sql);
		
		String rSql;
		do {
			rSql = sql;
			sql = chang(sql);
		} while (sql != null);
		sql = rSql;
		*/
		return sql;
	}
	
	/**
	 * sysdate 和now（）转化
	 * 
	 * @param sql
	 * @return
	 */
	public String changNvl(String sql) {
		Pattern p = Pattern.compile(
				"[\t\n,>< ()\\+\\-=]+nvl[ (]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String text = m.group().toLowerCase();
			m.appendReplacement(buf, text.replace("nvl", "ifnull"));
		}
		m.appendTail(buf);
		return buf.toString();
	}

	/**
	 * sysdate 和now（）转化
	 * 
	 * @param sql
	 * @return
	 */
	public String changSysDate(String sql) {
		Pattern p = Pattern.compile(
				"[\t\n,>< (\\+\\-=]+sysdate[\t\n,><) \\+\\-=]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String text = m.group().toLowerCase();
			m.appendReplacement(buf, text.replace("sysdate", "now()"));
		}
		m.appendTail(buf);
		return buf.toString();
	}

	/**
	 * 字符串拼接函数转化
	 * 
	 * @param sql
	 * @return
	 */
	public String chang(String sql) {
		Pattern p = Pattern.compile(
				"[-(),'_a-z0-9\\.\\?]+[ ]*\\|\\|+[ ]*[-(),'_a-zA-Z0-9\\.\\?]+",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer buf = new StringBuffer();
		if (m.find()) {
			String text = m.group();
			String[] strch = text.split("\\|\\|");
			m.appendReplacement(buf, "concat(" + strch[0].trim() + ","
					+ strch[1].trim() + ")");
			m.appendTail(buf);
			return buf.toString();
		}
		return null;
	}
}
