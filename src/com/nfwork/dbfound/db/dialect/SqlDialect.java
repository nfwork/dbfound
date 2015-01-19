package com.nfwork.dbfound.db.dialect;

public interface SqlDialect {
	
	/**
	 * 分页sql
	 * @param sql
	 * @param pagerSize 页面大小
	 * @param startWith 开始位置
	 * @return
	 */
	public String getPagerSql(String sql ,int pagerSize,long startWith) ;
	
	/**
	 * whensql ,条件过滤
	 * @param sql
	 */
	public String getWhenSql(String when) ;
	
	
	/**
	 * 根据方言格式化sql
	 * @param sql
	 * @return
	 */
	public String parseSql(String sql);
    
}
