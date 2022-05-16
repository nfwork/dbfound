package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
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
		} else {
			cursorValues = (List<Map>) context.getData(sourcePath);
		}
		if (cursorValues != null) {
			int i = 0;
			for (Map map : cursorValues) {
				for (Iterator iterator = map.entrySet().iterator(); iterator
						.hasNext();) {
					Map.Entry entry = (Map.Entry) iterator.next();
					String paramName = entry.getKey().toString();
					Param param = params.get(paramName);
					if(param == null){
						param = params.get(StringUtil.underscoreToCamelCase(paramName));
					}
					if (param != null) {
						param.setValue(entry.getValue());
						param.setSourcePathHistory("cursorlist[" + i + "]");
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
				colNames[i] = metaset.getColumnLabel(i).toLowerCase();
			}

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
					case Types.VARCHAR:
						mapdata.put(columnName, value);
						break;
					case Types.INTEGER:
						mapdata.put(columnName, dataset.getInt(i));
						break;
					case Types.DOUBLE:
						if (value.endsWith(".0")) {
							mapdata.put(columnName, dataset.getLong(i));
						} else {
							mapdata.put(columnName, dataset.getDouble(i));
						}
						break;
					case Types.FLOAT:
						if (value.endsWith(".0")) {
							mapdata.put(columnName, dataset.getInt(i));
						} else {
							mapdata.put(columnName, dataset.getFloat(i));
						}
						break;
					case Types.DECIMAL:
						mapdata.put(columnName, dataset.getDouble(i));
						break;
					case Types.NUMERIC:
						mapdata.put(columnName, dataset.getDouble(i));
						break;
					case Types.VARBINARY:
						mapdata.put(columnName, dataset.getLong(i));
						break;
					case Types.BIGINT:
						mapdata.put(columnName, dataset.getLong(i));
						break;
					case Types.REAL:
						if (value.endsWith(".0") || value.indexOf(".") == -1) {
							mapdata.put(columnName, dataset.getInt(i));
						} else {
							mapdata.put(columnName, dataset.getFloat(i));
						}
						break;
					case Types.BLOB:
						break;
					//case Types.CLOB: break;
					//case Types.NCLOB:break;
					case Types.LONGVARBINARY:
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
