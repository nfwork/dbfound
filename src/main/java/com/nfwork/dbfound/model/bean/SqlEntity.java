package com.nfwork.dbfound.model.bean;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.enums.EnumTypeHandler;
import com.nfwork.dbfound.util.*;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.base.Entity;
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

			initParamValue(nfParam);
			initParamType(nfParam);

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
				} else if(nfParam.getValue() instanceof Integer){
					statement.setInt(cursor,(Integer) nfParam.getValue());
				} else if(nfParam.getValue() instanceof Long){
					statement.setLong(cursor,(Long) nfParam.getValue());
				} else if(nfParam.getValue() instanceof Double){
					statement.setDouble(cursor,(Double) nfParam.getValue());
				} else if(nfParam.getValue() instanceof Float){
					statement.setFloat(cursor,(Float) nfParam.getValue());
				} else if(nfParam.getValue() instanceof Short){
					statement.setShort(cursor,(Short) nfParam.getValue());
				} else if(nfParam.getValue() instanceof BigDecimal){
					statement.setBigDecimal(cursor,(BigDecimal) nfParam.getValue());
				} else if(nfParam.getValue() instanceof Byte){
					statement.setByte(cursor,(Byte) nfParam.getValue());
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
				paramValue = paramValue.trim();
				if (nfParam.getValue() instanceof java.sql.Date) {
					java.sql.Date date = (java.sql.Date) nfParam.getValue();
					statement.setDate(cursor, date);
				} else if (nfParam.getValue() instanceof Date) {
					Date date = (Date) nfParam.getValue();
					statement.setTimestamp(cursor, new Timestamp(date.getTime()));
				} else if(nfParam.getValue() instanceof Long){
					statement.setTimestamp(cursor, new Timestamp((Long) nfParam.getValue()));
				} else if(paramValue.matches("[0123456789]*")){
					statement.setTimestamp(cursor, new Timestamp(Long.parseLong(paramValue)));
				} else if(paramValue.length() == DBFoundConfig.getDateFormat().length()){
					try {
						SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateFormat());
						statement.setDate(cursor, new java.sql.Date(format.parse(paramValue).getTime()));
					}catch (ParseException exception){
						throw new DBFoundRuntimeException("parse date exception, value :" + paramValue ,exception);
					}
				} else if(paramValue.length() == DBFoundConfig.getDateTimeFormat().length()){
					try {
						SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
						statement.setTimestamp(cursor, new Timestamp(format.parse(paramValue).getTime()));
					}catch (ParseException exception){
						throw new DBFoundRuntimeException("parse datetime exception, value :" + paramValue ,exception);
					}
				}else {
					statement.setString(cursor, paramValue);
				}
			} else if (paramDataType.equals("file")) {
				try {
					String saveType = nfParam.getFileSaveType();
					Object o = nfParam.getValue();
					if(o instanceof InputStream){
						InputStream inputStream = (InputStream) o;
						if ("db".equals(saveType)) {
							statement.setBinaryStream(cursor, inputStream);
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

			initParamValue(nfParam);
			initParamType(nfParam);

			paramValue = nfParam.getStringValue();

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

	/**
	 * 枚举类型 boolean类型支持 2022年07月08日17:26:06
	 * @param nfParam
	 */
	private void initParamValue(Param nfParam){
		Object object = nfParam.getValue();
		if(object instanceof Enum){
			EnumTypeHandler handler = EnumHandlerFactory.getEnumHandler(object.getClass());
			Object value = handler.getEnumValue(object);
			nfParam.setValue(value);
		}else if(object instanceof Boolean && "number".equals( nfParam.getDataType())){
			if ((Boolean) nfParam.getValue()){
				nfParam.setValue(1);
			} else{
				nfParam.setValue(0);
			}
		}
	}

	private void initParamType(Param nfParam){
		if ("unknown".equals(nfParam.getDataType())){
			Object value = nfParam.getValue();
			if (value != null){
				if (value instanceof Number){
					nfParam.setDataType("number");
				}else if(value instanceof Date){
					nfParam.setDataType("date");
				}else{
					nfParam.setDataType("varchar");
				}
			}
		}
	}

	public String[] getColNames(ResultSetMetaData metaset) throws SQLException {
		int size = metaset.getColumnCount();
		String colNames[] = new String[size];
		for (int i = 1; i <= colNames.length; i++) {
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
			colNames[i-1] = colName;
		}
		return  colNames;
	}

	public Object getData(String value, int columnType, ResultSet dataset, int index, Calendar defaultCalendar) throws SQLException {
		Object result ;
		switch (columnType) {
			case Types.INTEGER:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.BIT:
				result = dataset.getInt(index);
				break;
			case Types.BIGINT:
				result = dataset.getLong(index);
				break;
			case Types.FLOAT:
			case Types.REAL:
				if (value.endsWith(".0") || !value.contains(".")) {
					result = dataset.getInt(index);
				} else {
					result = dataset.getFloat(index);
				}
				break;
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
				if (value.endsWith(".0") || !value.contains(".")) {
					result = dataset.getLong(index);
				} else {
					result = dataset.getDouble(index);
				}
				break;
			case Types.VARBINARY:
				if (value.matches("[0123456789]*\\.[0123456789]+")) {
					result = dataset.getDouble(index);
				} else if (value.matches("[0123456789]*")) {
					result = dataset.getLong(index);
				} else {
					result = value;
				}
				break;
			case Types.DATE:
				result = dataset.getDate(index, defaultCalendar);
				break;
			case Types.TIME:
			case Types.TIMESTAMP:
				result = dataset.getTimestamp(index, defaultCalendar);
				break;
			case Types.BOOLEAN:
				result = dataset.getBoolean(index);
				break;
			default:
				result = value;
		}
		return result;
	}

	public void log(String sql, Map<String, Param> params) {
		LogUtil.log(sql, params.values());
	}

	public void log(String sql, List<Param> listParam) {
		LogUtil.log(sql, listParam);
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
