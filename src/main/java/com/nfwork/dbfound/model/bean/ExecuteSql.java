package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.ParseUtil;

public class ExecuteSql extends SqlEntity {

	private static final long serialVersionUID = 7525842037480200449L;

	private String generatedKeyParam;

	private String affectedCountParam;

	@Override
	public void run() {
		super.run();
		autoCreateParam(sql,this);
	}

	public void execute(Context context, Map<String, Param> params, String provideName) {

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		// 2012年8月14日22:01:04 添加静态参数设置
		sql = ParseUtil.parse(sql, params);
		// end 添加

		String esql = getExecuteSql(sql, params);
		// 方言处理
		esql = dialect.parseSql(esql);

		try {
			PreparedStatement statement = null;
			ResultSet rs = null;
			if (DataUtil.isNotNull(generatedKeyParam)){
				statement = conn.prepareStatement(esql, Statement.RETURN_GENERATED_KEYS);
			}else {
				statement = conn.prepareStatement(esql);
			}
			try {
				// 参数设定
				initParam(statement, sql, params);
				statement.execute();
				
				// 2013年9月9日8:47:54 添加主键返回
				if (DataUtil.isNotNull(generatedKeyParam)) {
					rs = statement.getGeneratedKeys();
					if (rs.next()) {
						Param param = params.get(generatedKeyParam);
						if (param == null) {
							throw new ParamNotFoundException("param: " + generatedKeyParam + " 没有定义");
						}
						param.setValue(rs.getLong(1));
						param.setSourcePathHistory("set by generatedKey");
					}
				}
				
				// 2014年8月14日11:38:34 添加affectedCountParam
				if (DataUtil.isNotNull(affectedCountParam)) {
					int fetchSize = statement.getUpdateCount();

					Param param = params.get(affectedCountParam);
					if (param == null) {
						throw new ParamNotFoundException("param: " + affectedCountParam + " 没有定义");
					}
					param.setValue(fetchSize);
					param.setSourcePathHistory("set by affectedCount");
				}
				
			} finally {
				DBUtil.closeResultSet(rs);
				DBUtil.closeStatement(statement);
				log(esql, params);
			}
			
		} catch (SQLException e) {
			throw new DBFoundPackageException("ExecuteSql执行异常:" + e.getMessage(), e);
		}
	}

	public String getGeneratedKeyParam() {
		return generatedKeyParam;
	}

	public void setGeneratedKeyParam(String generatedKeyParam) {
		this.generatedKeyParam = generatedKeyParam;
	}

	public String getAffectedCountParam() {
		return affectedCountParam;
	}

	public void setAffectedCountParam(String affectedCountParam) {
		this.affectedCountParam = affectedCountParam;
	}

}
