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
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;

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
	private String initError;

	@Override
	public void run() {
		super.run();
		if(DataUtil.isNull(where) || DataUtil.isNull(message)){
			initError = "CollisionSql attribute where and message can not be null";
			return;
		}
		autoCreateParam(where,this);
		autoCreateParam(message,this);
	}

	public void execute(Context context, Map<String, Param> params,
						String provideName){
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		String whereSql = staticParamParse(where, params);

		whereSql = dialect.getWhenSql(whereSql);
		String esql = getExecuteSql(whereSql, params);

		// 方言处理
		esql = dialect.parseSql(esql);
		PreparedStatement statement = null;
		ResultSet set = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, whereSql, params);
			set = statement.executeQuery();
			if (set.next()) {
				int flag = set.getInt(1);
				if (flag != 0) {
					throw new CollisionException(staticParamParse(message, params));
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
