package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.ParseUtil;

/**
 * 碰撞sql，检验where条件是否成立 标出message消息
 * 
 * @author Administrator
 * 
 */
public class CollisionSql extends SqlEntity {

	private static final long serialVersionUID = 2002950391423757459L;

	private String where;
	private String message;

	public void execute(Context context, Map<String, Param> params,
			String provideName){
		
		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		// 2012年8月14日22:01:04 添加静态参数设置
		where = ParseUtil.parse(where, params);
		// end 添加
		
		String sql = dialect.getWhenSql(where);
		String esql = getExecuteSql(sql, params);

		// 方言处理
		esql = dialect.parseSql(esql);
		PreparedStatement statement = null;
		ResultSet set = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, sql, params);
			set = statement.executeQuery();
			if (set.next()) {
				int flag = set.getInt(1);
				if (flag != 0) {
					throw new CollisionException(ParseUtil.parse(message, params));
				}
			}
		}catch (SQLException e) {
			throw new DBFoundPackageException(
					"CollisionSql执行异常:" + e.getMessage(), e);
		}
		finally {
			DBUtil.closeResultSet(set);
			DBUtil.closeStatement(statement);
			log(esql, params);
		}
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
