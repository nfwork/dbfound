package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.model.base.IOType;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;

public class ExecuteSql extends Sql {

	private static final long serialVersionUID = 7525842037480200449L;

	private String generatedKeyParam;

	private String affectedCountParam;

	private String initError;

	@Override
	public void run() {
		super.run();
		if(DataUtil.isNull(sql)){
			initError = "ExecuteSql content sql can not be null";
			return;
		}
		autoCreateParam(sql,this);
		if(sqlPartList!=null && !sqlPartList.isEmpty()){
			String tmp = sqlPartList.stream().map(v->v.getCondition()+","+v.getPart()).collect(Collectors.joining(","));
			autoCreateParam(tmp,this);
		}
	}

	public void execute(Context context, Map<String, Param> params, String provideName) {
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}

		String executeSql;
		if(sqlPartList != null && !sqlPartList.isEmpty()){
			params = new LinkedHashMap<>(params);
			executeSql = initSqlPart(sql,params,context,provideName);
		}else{
			executeSql = sql;
		}

		Connection conn = context.getConn(provideName);
		executeSql = staticParamParse(executeSql, params);

		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(executeSql, params, exeParam);

		PreparedStatement statement = null;
		ResultSet rs = null;

		try {
			if (DataUtil.isNotNull(generatedKeyParam)){
				statement = conn.prepareStatement(esql, Statement.RETURN_GENERATED_KEYS);
			}else {
				statement = conn.prepareStatement(esql);
			}

			// 参数设定
			initParam(statement, exeParam);
			statement.execute();

			// 2013年9月9日8:47:54 添加主键返回
			if (DataUtil.isNotNull(generatedKeyParam)) {
				rs = statement.getGeneratedKeys();
				if (rs.next()) {
					Param param = params.get(generatedKeyParam);
					if (param == null) {
						throw new ParamNotFoundException("param: " + generatedKeyParam + " not defined");
					}
					param.setRequireLog(true);
					param.setValue(rs.getLong(1));
					param.setSourcePathHistory("set_by_generatedKey");

					if(param.getIoType() != IOType.IN){
						context.setOutParamData(param.getName(),param.getValue());
					}
				}
			}

			// 2014年8月14日11:38:34 添加affectedCountParam
			if (DataUtil.isNotNull(affectedCountParam)) {
				int fetchSize = statement.getUpdateCount();

				Param param = params.get(affectedCountParam);
				if (param == null) {
					throw new ParamNotFoundException("param: " + affectedCountParam + " not defined");
				}
				param.setRequireLog(true);
				param.setValue(fetchSize);
				param.setSourcePathHistory("set_by_affectedCount");

				if(param.getIoType() != IOType.IN){
					context.setOutParamData(param.getName(),param.getValue());
				}
			}
		}catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"ExecuteSql"), esql, e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(rs);
			DBUtil.closeStatement(statement);
			log("executeSql",esql, params);
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
