package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.ParseUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;

public class QuerySql extends SqlEntity {

	private static final long serialVersionUID = -8182147424516469176L;

	public void execute(Context context, Map<String, Param> params, String provideName) {

		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		// 2012年8月14日22:01:04 添加静态参数设置
		sql = ParseUtil.parse(sql, params);
		// end 添加
		String esql = getExecuteSql(sql, params);

		// 方言处理
		esql = dialect.parseSql(esql);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, sql, params);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();

			if (dataset.next()) {
				for (int i = 1; i <= metaset.getColumnCount(); i++) {
					String value = dataset.getString(i);
					String columnName = metaset.getColumnLabel(i).toLowerCase();

					Param param = params.get(columnName);
					if (param == null) {
						throw new ParamNotFoundException("param: " + columnName + " 没有定义");
					}
					String paramType = param.getDataType();
					if ("varchar".equals(paramType)) {
						param.setValue(value);
					} else if ("number".equals(paramType)) {
						if (value == null || value.endsWith(".0") || value.indexOf(".") == -1) {
							param.setValue(dataset.getLong(i));
						} else {
							param.setValue(dataset.getDouble(i));
						}
					} else if ("date".equals(paramType)) {
						Timestamp timestamp = dataset.getTimestamp(i, Calendar.getInstance());
						if (timestamp == null) {
							param.setValue(timestamp);
						} else {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(timestamp);

							if (timestamp != null && calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0
									&& calendar.get(Calendar.SECOND) == 0) {
								param.setValue(dataset.getDate(i, Calendar.getInstance()));
							} else {
								param.setValue(timestamp);
							}
						}
					} else if ("file".equals(paramType)) {
						blobExecute(columnName, dataset, params, param, i);
					}
					param.setSourcePathHistory("querySql");
				}
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("querySql执行异常:" + e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			log(esql, params);
		}
	}

	private void blobExecute(String columnName, ResultSet dataset, Map<String, Param> params, Param param, int index) {
		try {

			if ("out".equals(param.getIoType())) {
				if ("db".equals(param.getFileSaveType())) {
					OutputStream out = null;
					InputStream in = dataset.getBinaryStream(index);
					try {
						if (in != null) {
							String fileName = UUIDUtil.getUUID() + ".dbf";
							File file = new File(FileUtil.getUploadFolder(null), fileName);
							out = new FileOutputStream(file);
							byte b[] = new byte[2048];
							int i = in.read(b);
							while (i != -1) {
								out.write(b, 0, i);
								i = in.read(b);
							}
							param.setValue(file);
						}
					} finally {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.flush(); // 输入完毕，清除缓冲
							out.close();
						}
					}
				} else {
					param.setValue(dataset.getString(index));
				}
			}
		} catch (Exception e) {
			throw new DBFoundPackageException("lob 字段处理异常:" + e.getMessage(), e);
		}
	}
}
