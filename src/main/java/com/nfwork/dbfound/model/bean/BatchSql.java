package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;

/**
 * 批量从数据库查询数据，作为结果集 然后执行
 * 
 * @author John
 * 
 */
public class BatchSql extends Sqls {
	private String cursor;
	private String cursorRootPath = "param.cursorList";
	private String sourcePath;

	private String initError;

	private String item;

	private String index;

	@Override
	public void doEndTag() {
		super.doEndTag();
		if(DataUtil.isNull(sourcePath) && DataUtil.isNull(cursor)){
			initError = "BatchSql attribute sourcePath and cursor, can not be null on the same time";
			return;
		}
		if(DataUtil.isNotNull(cursor)) {
			cursor = StringUtil.sqlFullTrim(cursor);
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
			List<Map<String,Object>> cursorValues = new ArrayList<>();
			executeCursor(context, params, provideName, cursorValues);
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

		Object rootData = context.getData(exeSourcePath);
		int dataSize =  DataUtil.getDataLength(rootData);
		if(dataSize <= 0){
			return;
		}
		if(rootData instanceof Collection && !(rootData instanceof ArrayList)){
			rootData = ((Collection<?>)rootData).toArray();
		}

		List<Param> paramList = new ArrayList<>();
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

		for (int i=0 ; i < dataSize ; i++) {
			String currentPath = exeSourcePath +"[" + i +"]";

			//执行过程中改变currentPath
			context.setCurrentPath(currentPath);

			Object currentData = DBFoundEL.getDataByIndex(i,rootData);

			for (Param param : paramList){
				String sp = DataUtil.isNull(param.getSourcePath())?param.getName():param.getSourcePath();
				if (index != null && index.equals(sp)) {
					param.setValue(i);
					param.setSourcePathHistory("set_by_index");
				}else {
					String sph;
					Object value;
					if (item != null && item.equals(sp)) {
						sph = currentPath;
						value = currentData;
					} else {
						sph = currentPath + "." + sp;
						value = DBFoundEL.getData(sp, currentData);
					}
					if ("".equals(value) && param.isEmptyAsNull()) {
						value = null;
					}
					param.setSourcePathHistory(sph);
					param.setValue(value);
				}
			}
			for (SqlEntity sql : sqlList) {
				sql.execute(context, params, provideName);
			}
		}

		//执行完成后恢复原有currentPath
		context.setCurrentPath(inCurrentPath);

	}


	public void executeCursor(Context context, Map<String, Param> params,String provideName, List<Map<String,Object>> cursorValues){
		Connection conn = context.getConn(provideName);

		String cursorSql = staticParamParse(cursor, params);
		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(cursorSql, params, exeParam);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();

			// 得到元数据
			String[] colNames = getColNames(metaset);

			Calendar defaultCalendar = Calendar.getInstance();

			while (dataset.next()) {
				Map<String, Object> mapdata = new HashMap<>();
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
		}catch (SQLException e){
			throw new SqlExecuteException(provideName, getSqlTask(context,"BatchSql"), esql, e.getMessage(), e);
		}finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			log("batchCursorSql",esql, params,exeParam);
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

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
}
