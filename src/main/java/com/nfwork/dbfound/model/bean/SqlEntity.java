package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.JsonUtil;
import org.apache.commons.fileupload.FileItem;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.base.Entity;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.file.FileUtil;

public abstract class SqlEntity extends Sqls {

	private static final long serialVersionUID = 3035666882993092230L;

	String sql;

	protected final static String paramReplace = "\\{\\@[ a-zA-Z_0-9\u4E00-\u9FA5]*\\}";

	protected final static String dynamicReplace = "\\$" + paramReplace;

	protected final static String staticReplace = "\\#" + paramReplace;


	@Override
	public void run() {
		Entity entity = getParent();
		if (entity instanceof Sqls) {
			Sqls sqls = (Sqls) entity;
			sqls.sqlList.add(this);
		}
	}

	@Override
	public void init(Element element) {
		sql = element.getTextTrim();
		super.init(element);
	}

	public abstract void execute(Context context, Map<String, Param> params, String provideName);

	/**
	 * 得到最后执行的sql语句
	 * 
	 * @return
	 */
	public String getExecuteSql(String sql, Map<String, Param> params) {
		String executeSqlString = sql;
		// 转化执行的sql
		executeSqlString = executeSqlString.replaceAll(dynamicReplace, "?");
		return executeSqlString;
	}

	/**
	 * 自动补齐 Param定义
	 * since 2.5.0
	 */
	public void autoCreateParam(String sql, Map<String, Param> params) {
		// 设定参数
		Pattern p = Pattern.compile(paramReplace);
		Matcher m = p.matcher(sql);
		int cursor = 1; // 游标记录参数的位置
		while (m.find()) {
			String paramName = m.group();
			String name = paramName.substring(2, paramName.length() - 1).trim();
			if(params.get(name)==null) {
				Param nfParam = new Param();
				nfParam.setName(name);
				nfParam.setDataType("unknown");
				params.put(name, nfParam);
			}
		}
	}

	public void autoCreateParam(String sql, Entity entity) {
		if (DataUtil.isNull(sql))return;
		Entity entityParent = entity.getParent();
		while (entityParent != null){
			if ( entityParent instanceof  Execute){
				Execute execute = (Execute) entityParent;
				autoCreateParam(sql, execute.getParams());
				break;
			}else{
				entityParent = entityParent.getParent();
			}
		}
	}

	/**
	 * 参数设定 sql为原生sql语句，用来寻找参数的位置
	 * 
	 * @throws SQLException
	 * @throws NumberFormatException
	 */
	public void initParam(PreparedStatement statement, String sql, Map<String, Param> params)
			throws NumberFormatException, SQLException {

		String paramValue;
		String paramDataType;

		// 设定参数
		Pattern p = Pattern.compile(dynamicReplace);
		Matcher m = p.matcher(sql);
		int cursor = 1; // 游标记录参数的位置
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " not defined");
			}

			initStaticParam(nfParam);

			if ("true".equals(nfParam.getUUID())) {
				paramValue = UUIDUtil.getUUID();
				nfParam.setValue(paramValue);
			} else {
				paramValue = nfParam.getStringValue();
			}
			paramDataType = nfParam.getDataType();

			if (paramValue == null) {
				statement.setString(cursor, null);
			} else if (paramDataType.equals("varchar")) {
				if (nfParam.getValue() instanceof Map ){
					statement.setString(cursor, JsonUtil.mapToJson((Map)nfParam.getValue()));
				}else if( nfParam.getValue() instanceof Set ){
					statement.setString(cursor,JsonUtil.setToJson((Set)nfParam.getValue()));
				}else if( nfParam.getValue() instanceof List ){
					statement.setString(cursor,JsonUtil.listToJson((List)nfParam.getValue()));
				}else if( nfParam.getValue() instanceof Object[]){
					statement.setString(cursor,JsonUtil.arrayToJson((Object[])nfParam.getValue()));
				}else {
					statement.setString(cursor, paramValue);
				}
			} else if (paramDataType.equals("number")) {
				if ("".equals(paramValue.trim())) {
					statement.setString(cursor, null);
				} else if (!paramValue.contains(".")) {
					statement.setLong(cursor, Long.parseLong(paramValue));
				} else if (paramValue.endsWith(".0")) {
					paramValue = paramValue.substring(0, paramValue.length() - 2);
					nfParam.setValue(paramValue);
					statement.setLong(cursor, Long.parseLong(paramValue));
				} else {
					statement.setDouble(cursor, Double.parseDouble(paramValue));
				}
			} else if (paramDataType.equals("date")) {
				if (nfParam.getValue() instanceof java.sql.Date) {
					java.sql.Date date = (java.sql.Date) nfParam.getValue();
					statement.setDate(cursor, date);
				} else if (nfParam.getValue() instanceof Date) {
					Date date = (Date) nfParam.getValue();
					statement.setTimestamp(cursor, new Timestamp(date.getTime()));
				} else {
					statement.setString(cursor, paramValue);
				}
			} else if (paramDataType.equals("file")) {
				try {
					String saveType = nfParam.getFileSaveType();
					Object o = nfParam.getValue();
					if (o != null) {
						if(o instanceof FileItem){
							FileItem item = (FileItem) o;
							if ("db".equals(saveType)) {
								statement.setBinaryStream(cursor, item.getInputStream(), (int) item.getSize());
							} else {
								String filename = UUIDUtil.getUUID() + ".uf";
								String fileFoldName = FileUtil.getUploadFolderName();
								item.write(new File(FileUtil.getUploadFolder(fileFoldName), filename));
								statement.setString(cursor, fileFoldName + "/" + filename);
							}
						}else if(o instanceof File){
							File file = (File) o;
							if ("db".equals(saveType)) {
								statement.setBinaryStream(cursor, new FileInputStream(file), file.length());
							} 
						}else if(o instanceof InputStream){
							InputStream inputStream = (InputStream) o;
							if ("db".equals(saveType)) {
								statement.setBinaryStream(cursor, inputStream);
							} 
						}
					}
				} catch (Exception e) {
					LogUtil.error(e.getMessage(), e);
				}
			} else {
				statement.setString(cursor, paramValue);
			}
			cursor++;
		}
	}

	/**
	 * 静态参数 初始化
	 * @param sql
	 * @param params
	 * @return
	 */
	public String staticParamParse(String sql, Map<String, Param> params) {
		if (sql == null || "".equals(sql)) {
			return "";
		}
		String paramValue;

		Pattern p = Pattern.compile(staticReplace);
		Matcher m = p.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " not defined");
			}
			paramValue = nfParam.getStringValue();

			initStaticParam(nfParam);

			// UUID取值
			if ("true".equals(nfParam.getUUID())) {
				paramValue = UUIDUtil.getUUID();
			}else if (paramValue == null) {
				paramValue = "";
			}else {
				paramValue = paramValue.replace("$", "\\$");
			}
			m.appendReplacement(buf, paramValue);
		}
		m.appendTail(buf);
		return buf.toString();
	}

	private void initStaticParam(Param nfParam){
		if ("unknown".equals(nfParam.getDataType())){
			Object value = nfParam.getValue();
			if (value != null){
				if (value instanceof Integer || value instanceof Long
						|| value instanceof Double || value instanceof  Float){
					nfParam.setDataType("number");
				}else if(value instanceof Date){
					nfParam.setDataType("date");
				}else{
					nfParam.setDataType("varchar");
				}
			}
		}
	}

	public void log(String sql, Map<String, Param> params) {
		LogUtil.log(sql, params);
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	public List<SqlEntity> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<SqlEntity> sqlList) {
		this.sqlList = sqlList;
	}

}
