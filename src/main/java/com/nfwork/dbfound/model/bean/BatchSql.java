package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;

/**
 * 批量从数据库查询数据，作为结果集 然后执行
 * 
 * @author John
 * 
 */
public class BatchSql extends SqlEntity {

	private static final long serialVersionUID = 5145846152572625196L;

	private String cursor;
	private String cursorRootPath = "param.cursorList";
	private String sourcePath;

	private String initError;

	@Override
	public void run() {
		super.run();
		if(DataUtil.isNull(sourcePath) && DataUtil.isNull(cursor)){
			initError = "BatchSql attribute sourcePath and cursor, can not be null on the same time";
			return;
		}
		if(DataUtil.isNotNull(cursor)) {
			cursor = StringUtil.fullTrim(cursor);
			autoCreateParam(cursor, this);
		}
	}

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName) {
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}

		final String inCurrentPath = context.getCurrentPath();

		String exeSourcePath = sourcePath;

		// 执行游标得到相应的值
		if (cursor != null) {
			List<Map> cursorValues = new ArrayList<Map>();
			try {
				executeCursor(context, params, provideName, cursorValues);
			} catch (SQLException e) {
				throw new DBFoundPackageException("cursor sql execute exception:" + e.getMessage(), e);
			}
			if(DataUtil.isNull(cursorRootPath)){
				throw new DBFoundRuntimeException("cursorRootPath can not be null");
			}
			exeSourcePath = cursorRootPath;
			if(!ELEngine.isAbsolutePath(exeSourcePath)) {
				exeSourcePath = inCurrentPath +"." +exeSourcePath;
			}
			context.setData(exeSourcePath, cursorValues);
		}

		//判断是否相对路径,如果是相对路径则进行转化
		if(!ELEngine.isAbsolutePath(exeSourcePath)) {
			exeSourcePath = inCurrentPath +"." +exeSourcePath;
		}

		int dataSize = context.getDataLength(exeSourcePath);

		if(dataSize <= 0){
			return;
		}

		List<Param> paramList = new ArrayList<Param>();
		for (Param param : params.values()){
			if(!param.isBatchAssign()){
				continue;
			}
			if (DataUtil.isNotNull(param.getScope())){
				param.setBatchAssign(false);
				continue;
			}
			if (ELEngine.isAbsolutePath(param.getSourcePath())) {
				param.setBatchAssign(false);
				continue;
			}
			paramList.add(param);
		}

		Map<String, Object> elCache = new HashMap<>();

		for (int i=0 ; i < dataSize ; i++) {
			String currentPath = exeSourcePath +"[" + i +"]";

			//执行过程中改变currentPath
			context.setCurrentPath(currentPath);

			for (Param param : paramList){
				String sp = param.getSourcePath()==null?param.getName():param.getSourcePath();
				param.setSourcePathHistory(currentPath +"."+sp);
				Object value = context.getData(param.getSourcePathHistory(), elCache);
				if("".equals(value)){
					value = null;
				}
				param.setValue(value);
			}
			for (SqlEntity sql : sqlList) {
				sql.execute(context, params, provideName);
			}
		}

		//执行完成后恢复原有currentPath
		context.setCurrentPath(inCurrentPath);

	}


	public void executeCursor(Context context, Map<String, Param> params,String provideName, List<Map> cursorValues) throws SQLException {
		Connection conn = context.getConn(provideName);

		String cursorSql = staticParamParse(cursor, params, context);
		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(cursorSql, params, exeParam, context);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();

			// 得到元数据
			String colNames[] = getColNames(metaset);

			Calendar defaultCalendar = Calendar.getInstance();

			while (dataset.next()) {
				Map<String, Object> mapdata = new HashMap<String, Object>();
				for (int i = 1; i <= colNames.length; i++) {
					String columnName = colNames[i-1];
					if (dataset.getObject(i) == null) {
						mapdata.put(columnName, null);
						continue;
					}
					int columnType = metaset.getColumnType(i);
					mapdata.put(columnName,getData(columnType,dataset,i,defaultCalendar));
				}
				cursorValues.add(mapdata);
			}
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			log("batchCursorSql",esql, params);
		}
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getCursorRootPath() {
		return cursorRootPath;
	}

	public void setCursorRootPath(String cursorRootPath) {
		this.cursorRootPath = cursorRootPath;
	}
}
