package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.base.IOType;
import com.nfwork.dbfound.util.DBUtil;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;

public class QuerySql extends Sql {

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
		if(sqlPartList!=null && !sqlPartList.isEmpty()){
			String tmp = sqlPartList.stream().map(v->v.getCondition()+","+v.getPart()).collect(Collectors.joining(","));
			autoCreateParam(tmp,this);
		}
	}

	public void execute(Context context, Map<String, Param> params, String provideName) {
		if(initError != null){
			throw new DBFoundRuntimeException(initError);
		}
		String querySql;
		if(sqlPartList != null && !sqlPartList.isEmpty()){
			params = new LinkedHashMap<>(params);
			querySql = initSqlPart(sql,params,context,provideName);
		}else{
			querySql = sql;
		}

		Connection conn = context.getConn(provideName);

		querySql = staticParamParse(querySql, params);

		List<Object> exeParam = new ArrayList<>();
		String esql = getExecuteSql(querySql, params, exeParam);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(esql);
			// 参数设定
			initParam(statement, exeParam);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();
			Calendar defaultCalendar = Calendar.getInstance();
			int fileSize = 0;

			if (dataset.next()) {
				for (int i = 1; i <= metaset.getColumnCount(); i++) {
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
					int columnType = metaset.getColumnType(i);

					if (paramType == DataType.FILE && (columnType != Types.CHAR && columnType !=Types.VARCHAR)) {
						//querySQL outParam下载文件，仅支持一个File
						if (param.getIoType() == IOType.OUT && fileSize == 0) {
							blobExecute(dataset, param, i);
							fileSize = 1;
						}
					}else{
						if (dataset.getObject(i) == null) {
							param.setValue(null);
						}else{
							param.setValue(getData(columnType,dataset,i,defaultCalendar));
						}
					}
					param.setSourcePathHistory("set_by_querySql");
					param.setRequireLog(true);

					if(param.getIoType() != IOType.IN){
						context.setOutParamData(param.getName(),param.getValue());
					}
				}
			}
		} catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"QuerySql"), esql, e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			log("querySql",esql, params);
		}
	}

	private void blobExecute(ResultSet dataset, Param param, int index) {
		try {
			String fileName = UUIDUtil.getUUID() + ".lob.dbf";
			File file = new File(FileUtil.getUploadFolder(null), fileName);

			try (InputStream in = dataset.getBinaryStream(index);
				 OutputStream out = Files.newOutputStream(file.toPath())) {
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
		} catch (Exception e) {
			throw new DBFoundPackageException("lob field execute exception:" + e.getMessage(), e);
		}
	}
}
