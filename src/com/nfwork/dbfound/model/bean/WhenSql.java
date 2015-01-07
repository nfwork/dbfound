package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.ParseUtil;

public class WhenSql extends SqlEntity {

	private static final long serialVersionUID = 1781803860305201223L;

	private String when;

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName) {
		// 执行相应操作
		if (fitWhen(context, params, provideName)) {
			for (SqlEntity sql : sqlList) {
				sql.execute(context, params, provideName);
			}
		}
	}

	// 判定条件是否成立
	public boolean fitWhen(Context context, Map<String, Param> params,
			String provideName) {

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		// 2012年8月14日22:01:04 添加静态参数设置
		when = ParseUtil.parse(when, params);
		// end 添加

		String sql = dialect.getWhenSql(when);
		String esql = getExecuteSql(sql, params);

		// 方言处理
		esql = dialect.parseSql(esql);

		PreparedStatement statement = null;
		ResultSet set = null;
		int flag = 0;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, sql, params);
			set = statement.executeQuery();
			if (set.next()) {
				flag = set.getInt(1);
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("whenSql执行异常:" + e.getMessage(),
					e);
		}

		finally {
			DBUtil.closeResultSet(set);
			DBUtil.closeStatement(statement);
			log(esql, params);
		}
		if (flag != 0) {
			return true;
		}
		return false;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

}
