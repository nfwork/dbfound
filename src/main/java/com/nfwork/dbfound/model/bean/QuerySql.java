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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
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
		String querySql = staticParamParse(sql, params, context);

		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(querySql, params, exeParam, context);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
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
						String newName = underscoreToCamelCase(columnName);
						param = params.get(newName);
					}

					if (param == null) {
						param = new Param();
						param.setName(columnName);
						param.setDataType(DataType.UNKNOWN);
						params.put(columnName,param);
					}

					DataType paramType = param.getDataType();
					if (paramType == DataType.FILE) {
						blobExecute(dataset, param, i);
					}else{
						int columnType = metaset.getColumnType(i);
						param.setValue(getData(value,columnType,dataset,i,defaultCalendar));
					}
					param.setSourcePathHistory("set_by_querySql");
					param.setRequireLog(true);

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
			log("querySql",esql, params, context);
		}
	}

	private void blobExecute(ResultSet dataset, Param param, int index) {
		try {
			if ("out".equals(param.getIoType())) {
				if ("db".equals(param.getFileSaveType())) {

					String fileName = UUIDUtil.getUUID() + ".dbf";
					File file = new File(FileUtil.getUploadFolder(null), fileName);

					try (InputStream in = dataset.getBinaryStream(index);
						 OutputStream out = new FileOutputStream(file)) {
						if (in != null) {
							byte[] b = new byte[4096];
							int i = in.read(b);
							while (i != -1) {
								out.write(b, 0, i);
								i = in.read(b);
							}
							param.setValue(file);
							out.flush();
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
