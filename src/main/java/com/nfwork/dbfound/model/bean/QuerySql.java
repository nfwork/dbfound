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
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;

public class QuerySql extends SqlEntity {

	private static final long serialVersionUID = -8182147424516469176L;
	private String initError;

	@Override
	public void run() {
		super.run();
		if(DataUtil.isNull(sql)){
			initError = "QuerySql content sql can not be null";
			return;
		}
		autoCreateParam(sql,this);
	}

	public void execute(Context context, Map<String, Param> params, String provideName) {
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}
		Connection conn = context.getConn(provideName);
		String querySql = staticParamParse(sql, params);
		String esql = getExecuteSql(querySql, params);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, querySql, params);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();
			Calendar defaultCalendar = Calendar.getInstance();

			if (dataset.next()) {
				for (int i = 1; i <= metaset.getColumnCount(); i++) {
					String value = dataset.getString(i);

					String columnName = metaset.getColumnName(i);

					// 判断是否有as 逻辑，如果没有as，强制转化为小写
					String labName =  metaset.getColumnLabel(i);
					if (labName.equalsIgnoreCase(columnName)){
						columnName = columnName.toLowerCase();
					}else{
						columnName = labName;
					}

					Param param = params.get(columnName);
					if (param == null){
						String newName = StringUtil.underscoreToCamelCase(columnName);
						param = params.get(newName);
					}

					if (param == null) {
						param = new Param();
						param.setName(columnName);
						param.setDataType("unknown");
						params.put(columnName,param);
					}

					String paramType = param.getDataType();
					if ("file".equals(paramType)) {
						blobExecute(dataset, param, i);
					}else{
						int columnType = metaset.getColumnType(i);
						param.setValue(getData(value,columnType,dataset,i,defaultCalendar));
					}
					param.setSourcePathHistory("querySql");

					if(!"in".equals(param.getIoType())){
						context.setOutParamData(param.getName(),param.getValue());
					}
				}
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("querySql execute exception:" + e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			log(esql, params);
		}
	}

	private void blobExecute(ResultSet dataset, Param param, int index) {
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
			throw new DBFoundPackageException("lob field execute exception:" + e.getMessage(), e);
		}
	}
}
