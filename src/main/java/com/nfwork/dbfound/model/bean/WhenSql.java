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
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.model.dsql.DSqlConfig;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;

public class WhenSql extends Sqls {

	private static final long serialVersionUID = 1781803860305201223L;

	private String when;
	private String initError;
	private boolean useDSql = false;
	private OtherwiseSql otherwiseSql;

	@Override
	public void run() {
		super.run();
		if(DataUtil.isNull(when)){
			initError = "WhenSql attribute when can not be null";
			return;
		}
		when = StringUtil.fullTrim(when);
		useDSql = !when.toLowerCase().contains("select ");
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
		}else{
			if(otherwiseSql != null){
				otherwiseSql.execute(context,params,provideName);
			}
		}
	}

	// 判定条件是否成立
	private boolean fitWhen(Context context, Map<String, Param> params,String provideName) {

		String whenSql = staticParamParse(when, params);
		List<Object> exeParam = new ArrayList<>();
		String eSql = getExecuteSql(whenSql, params, exeParam);

		if(DSqlConfig.isOpenDSql() && useDSql){
			try {
				boolean result  = DSqlEngine.checkWhenSql(eSql,exeParam,provideName,context);
				log("when dSql", "select " + eSql, params);
				return result;
			}catch (DSqlNotSupportException ignore){
			}
		}

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);
		eSql = dialect.getWhenSql(eSql);

		PreparedStatement statement = null;
		ResultSet set = null;
		int flag = 0;
		try {
			statement = conn.prepareStatement(eSql);
			// 参数设定
			initParam(statement, exeParam);
			set = statement.executeQuery();
			if (set.next()) {
				flag = set.getInt(1);
			}
		} catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"WhenSql"), eSql, e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(set);
			DBUtil.closeStatement(statement);
			log("whenSql",eSql, params);
		}
		return flag != 0;
	}

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

	public OtherwiseSql getOtherwiseSql() {
		return otherwiseSql;
	}

	public void setOtherwiseSql(OtherwiseSql otherwiseSql) {
		this.otherwiseSql = otherwiseSql;
	}
}
