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
import com.nfwork.dbfound.exception.CollisionException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.model.dsql.DSqlConfig;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;

/**
 * 碰撞sql，检验where条件是否成立 标出message消息
 * 
 * @author Administrator
 * 
 */
public class CollisionSql extends SqlEntity {
	private String where;
	private String message;
	private String code;
	private String initError;
	private boolean useDSql = false;

	@Override
	public void doEndTag() {
		super.doEndTag();
		if(DataUtil.isNull(where) || DataUtil.isNull(message)){
			initError = "CollisionSql attribute where and message can not be null";
			return;
		}
		where = StringUtil.sqlFullTrim(where);
		useDSql = !where.toLowerCase().contains("select ");
		autoCreateParam(where,this);
		autoCreateParam(message,this);
	}

	public void execute(Context context, Map<String, Param> params, String provideName){
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}

		String whereSql = staticParamParse(where, params);
		List<Object> exeParam = new ArrayList<>();
		String eSql = getExecuteSql(whereSql, params, exeParam);

		if( DSqlConfig.isOpenDSql() && useDSql){
			try {
				boolean result  = DSqlEngine.checkWhenSql(eSql,exeParam,provideName,context);
				log("collision dSql","select "+eSql, params,exeParam);
				if(result) {
					throw new CollisionException(staticParamParse(message, params),code);
				}else{
					return;
				}
			}catch (DSqlNotSupportException ignore){
			}
		}

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);
		eSql = dialect.getWhenSql(eSql);

		PreparedStatement statement = null;
		ResultSet set = null;
		try {
			statement = conn.prepareStatement(eSql);
			// 参数设定
			initParam(statement, exeParam);
			set = statement.executeQuery();
			if (set.next()) {
				int flag = set.getInt(1);
				if (flag != 0) {
					throw new CollisionException(staticParamParse(message, params),code);
				}
			}
		}catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"CollisionSql"), eSql, e.getMessage(), e);
		}finally {
			DBUtil.closeResultSet(set);
			DBUtil.closeStatement(statement);
			log("collisionSql",eSql, params, exeParam);
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
