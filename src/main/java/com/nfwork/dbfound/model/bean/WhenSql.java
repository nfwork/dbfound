package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;

public class WhenSql extends SqlEntity {

	private static final long serialVersionUID = 1781803860305201223L;

	private String when;
	private String initError;

	@Override
	public void run() {
		super.run();
		if(DataUtil.isNull(when)){
			initError = "WhenSql attribute when can not be null";
			return;
		}
		autoCreateParam(when,this);
	}

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName) {
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}
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

		String whenSql = staticParamParse(when, params);
		whenSql = dialect.getWhenSql(whenSql);

		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(whenSql, params, exeParam);

		PreparedStatement statement = null;
		ResultSet set = null;
		int flag = 0;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
			set = statement.executeQuery();
			if (set.next()) {
				flag = set.getInt(1);
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("whenSql execute exception:" + e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(set);
			DBUtil.closeStatement(statement);
			log(esql, params);
		}
		return flag != 0;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

}
