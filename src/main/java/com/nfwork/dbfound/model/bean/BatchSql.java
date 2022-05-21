package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.util.DBUtil;
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
	private String sourcePath;

	@Override
	public void run() {
		super.run();
		autoCreateParam(sql,this);
	}

	@Override
	public void execute(Context context, Map<String, Param> params,
			String provideName) {

		List<Map> cursorValues = null;
		// 执行游标得到相应的值
		if (cursor != null) {
			cursorValues = new ArrayList<Map>();
			try {
				executeCursor(context, params, provideName, cursorValues);
			} catch (SQLException e) {
				throw new DBFoundPackageException(
						"游标sql执行异常:" + e.getMessage(), e);
			}
			sourcePath = "cursorlist";
		} else {
			cursorValues = (List<Map>) context.getData(sourcePath);
		}
		if (cursorValues != null) {
			int i = 0;
			Set<String> nameSet = new HashSet<String>();

			for (Map map : cursorValues) {
				nameSet.addAll((Set<String>) map.keySet());

				for (String paramName : nameSet) {
					Param param = params.get(paramName);
					if(param == null){
						param = params.get(StringUtil.underscoreToCamelCase(paramName));
					}
					if (param != null) {
						param.setValue(map.get(paramName));
						param.setSourcePathHistory( sourcePath + "[" + i + "]."+ paramName);
					}
				}
				i++;
				for (SqlEntity sql : sqlList) {
					sql.execute(context, params, provideName);
				}
			}
		}
	}

	/**
	 * 执行cursor查询 将结果放到cursorValues
	 * @param context
	 * @param params
	 * @param provideName
	 * @param cursorValues
	 * @throws SQLException
	 */
	public void executeCursor(Context context, Map<String, Param> params,
			String provideName, List<Map> cursorValues) throws SQLException {

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		cursor = staticParamParse(cursor, params);

		String esql = getExecuteSql(cursor, params);

		// 方言处理
		esql = dialect.parseSql(esql);

		PreparedStatement statement = conn.prepareStatement(esql);
		ResultSet dataset = null;
		try {
			// 参数设定
			initParam(statement, cursor, params);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();

			// 得到元数据
			int size = metaset.getColumnCount();
			String colNames[] = new String[size + 1];
			for (int i = 1; i < colNames.length; i++) {
				String colName = metaset.getColumnName(i);

				// 判断是否有as 逻辑，如果没有as，强制转化为小写
				String labName =  metaset.getColumnLabel(i);
				if (labName.equalsIgnoreCase(colName)){
					colName = colName.toLowerCase();
				}else{
					colName = labName;
				}

				if (DBFoundConfig.isUnderscoreToCamelCase()){
					colName = StringUtil.underscoreToCamelCase(colName);
				}
				colNames[i] = colName;
			}

			Calendar defaultCalendar = Calendar.getInstance();

			while (dataset.next()) {
				Map<String, Object> mapdata = new HashMap<String, Object>();
				for (int i = 1; i <= size; i++) {
					String value = dataset.getString(i);
					String columnName = colNames[i];
					int columnType = metaset.getColumnType(i);
					if (value == null) {
						mapdata.put(columnName, null);
						continue;
					}
					switch (columnType) {
						case Types.INTEGER:
						case Types.TINYINT:
						case Types.SMALLINT:
							mapdata.put(columnName, dataset.getInt(i));
							break;
						case Types.BIGINT:
							mapdata.put(columnName, dataset.getLong(i));
							break;
						case Types.FLOAT:
						case Types.REAL:
							if (value.endsWith(".0") || !value.contains(".")) {
								mapdata.put(columnName, dataset.getInt(i));
							} else {
								mapdata.put(columnName, dataset.getFloat(i));
							}
							break;
						case Types.DOUBLE:
						case Types.DECIMAL:
						case Types.NUMERIC:
							if (value.endsWith(".0") || !value.contains(".")) {
								mapdata.put(columnName, dataset.getLong(i));
							} else {
								mapdata.put(columnName, dataset.getDouble(i));
							}
							break;
						case Types.VARBINARY:
							if (value.matches("[0123456789]*\\.[0123456789]+")) {
								mapdata.put(columnName, dataset.getDouble(i));
							} else if (value.matches("[0123456789]*")) {
								mapdata.put(columnName, dataset.getLong(i));
							} else {
								mapdata.put(columnName, value);
							}
							break;
						case Types.DATE:
							mapdata.put(columnName, dataset.getDate(i, defaultCalendar));
							break;
						case Types.TIME:
						case Types.TIMESTAMP:
							mapdata.put(columnName, dataset.getTimestamp(i, defaultCalendar));
							break;
						case Types.BOOLEAN:
							mapdata.put(columnName, dataset.getBoolean(i));
							break;
						default:
							mapdata.put(columnName, value);
					}
				}
				cursorValues.add(mapdata);
			}
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			log(esql, params);
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

}
