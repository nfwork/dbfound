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

import com.mysql.cj.x.protobuf.MysqlxExpr;
import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.enums.EnumTypeHandler;
import com.nfwork.dbfound.util.*;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.base.Entity;

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
		Pattern p = Pattern.compile(dynamicReplace);
		Matcher m = p.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " not defined");
			}

			StringBuilder value = new StringBuilder("?");
			if("collection".equals(nfParam.getDataType())){
				int length = DataUtil.getDataLength(nfParam.getValue());
				for(int i=1; i<length; i++){
					value.append(", ?");
				}
			}
			m.appendReplacement(buf, value.toString());
		}
		m.appendTail(buf);
		return buf.toString();
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

			//添加isIsCollection支持 2022年07月13日11:14:39
			if("collection".equals(nfParam.getDataType())){
				Object object = nfParam.getValue();
				int length = DataUtil.getDataLength(object);
				if(length < 1){
					throw new DBFoundRuntimeException("collection param, data size must > 1");
				}

				Map<String, Object> root = new HashMap<>();
				for (int i = 0; i < length; i++) {
					StringBuilder buffer = new StringBuilder();
					root.put("data", object);
					buffer.append("data[").append(i).append("].").append(nfParam.getInnerPath());

					Object value = DBFoundEL.getData(buffer.toString(), root);
					if (value instanceof Enum) {
						value = getEnumValue((Enum) value);
					}

					if(value == null){
						statement.setString(cursor,null);
					}else if(value instanceof String){
						statement.setString(cursor,(String)value);
					} else if(value instanceof Integer){
						statement.setInt(cursor,(Integer)value);
					} else if(nfParam.getValue() instanceof Long){
						statement.setLong(cursor,(Long) value);
					} else if(nfParam.getValue() instanceof Double){
						statement.setDouble(cursor,(Double) value);
					} else if(nfParam.getValue() instanceof Float){
						statement.setFloat(cursor,(Float) value);
					} else if(nfParam.getValue() instanceof Short){
						statement.setShort(cursor,(Short) value);
					} else if(nfParam.getValue() instanceof BigDecimal){
						statement.setBigDecimal(cursor,(BigDecimal) value);
					} else if(nfParam.getValue() instanceof Byte){
						statement.setByte(cursor,(Byte) value);
					} else if(nfParam.getValue() instanceof Boolean){
						statement.setBoolean(cursor,(Boolean) value);
					} else if (nfParam.getValue() instanceof java.sql.Date) {
						java.sql.Date date = (java.sql.Date) value;
						statement.setDate(cursor, date);
					} else if (nfParam.getValue() instanceof Date) {
						Date date = (Date) value;
						statement.setTimestamp(cursor, new Timestamp(date.getTime()));
					} else{
						statement.setString(cursor,value.toString());
					}
					cursor++;
				}
				continue;
			}

			if (nfParam.isUUID()) {
				nfParam.setValue(UUIDUtil.getUUID());
			}
			paramDataType = nfParam.getDataType();

			if (nfParam.getValue() == null) {
				statement.setString(cursor, null);
			} else if (paramDataType.equals("varchar")) {
				String paramValue;
				if(nfParam.getValue() instanceof String) {
					paramValue = nfParam.getValue().toString();
				}else if (nfParam.getValue() instanceof Map ){
					paramValue = JsonUtil.mapToJson((Map)nfParam.getValue());
					nfParam.setValue(paramValue);
				}else if( nfParam.getValue() instanceof Set ){
					paramValue = JsonUtil.setToJson((Set)nfParam.getValue());
					nfParam.setValue(paramValue);
				}else if( nfParam.getValue() instanceof List ){
					paramValue = JsonUtil.listToJson((List)nfParam.getValue());
					nfParam.setValue(paramValue);
				}else if( nfParam.getValue() instanceof Object[]){
					paramValue = JsonUtil.arrayToJson((Object[])nfParam.getValue());
					nfParam.setValue(paramValue);
				}else{
					paramValue = nfParam.getStringValue();
					nfParam.setValue(paramValue);
				}
				statement.setString(cursor, paramValue);
			} else if (paramDataType.equals("number")) {
				if(nfParam.getValue() instanceof Integer){
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
				} else {
					String paramValue = nfParam.getStringValue();
					if ("".equals(paramValue.trim())) {
						statement.setString(cursor, null);
					} else if (!paramValue.contains(".")) {
						nfParam.setValue(Long.parseLong(paramValue));
						statement.setLong(cursor, (Long) nfParam.getValue());
					} else if (paramValue.endsWith(".0")) {
						paramValue = paramValue.substring(0, paramValue.length() - 2);
						nfParam.setValue(Long.parseLong(paramValue));
						statement.setLong(cursor, (Long) nfParam.getValue());
					} else {
						nfParam.setValue(Double.parseDouble(paramValue));
						statement.setDouble(cursor, (Double) nfParam.getValue());
					}
				}
			} else if (paramDataType.equals("date")) {
				if (nfParam.getValue() instanceof java.sql.Date) {
					java.sql.Date date = (java.sql.Date) nfParam.getValue();
					statement.setDate(cursor, date);
				} else if (nfParam.getValue() instanceof Date) {
					Date date = (Date) nfParam.getValue();
					statement.setTimestamp(cursor, new Timestamp(date.getTime()));
				} else if(nfParam.getValue() instanceof Long){
					statement.setTimestamp(cursor, new Timestamp((Long) nfParam.getValue()));
				} else {
					String paramValue = nfParam.getStringValue().trim();
				    if (paramValue.matches("[0123456789]*")) {
						statement.setTimestamp(cursor, new Timestamp(Long.parseLong(paramValue)));
					} else if (paramValue.length() == DBFoundConfig.getDateFormat().length()) {
						try {
							SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateFormat());
							statement.setDate(cursor, new java.sql.Date(format.parse(paramValue).getTime()));
						} catch (ParseException exception) {
							throw new DBFoundRuntimeException("parse date exception, value :" + paramValue, exception);
						}
					} else if (paramValue.length() == DBFoundConfig.getDateTimeFormat().length()) {
						try {
							SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
							statement.setTimestamp(cursor, new Timestamp(format.parse(paramValue).getTime()));
						} catch (ParseException exception) {
							throw new DBFoundRuntimeException("parse datetime exception, value :" + paramValue, exception);
						}
					} else {
						statement.setString(cursor, paramValue);
					}
				}
			} else if(paramDataType.equals("boolean")){
				statement.setBoolean(cursor,(Boolean) nfParam.getValue());
			} else if (paramDataType.equals("file")) {
				String saveType = nfParam.getFileSaveType();
				if ("db".equals(saveType)) {
					InputStream inputStream = (InputStream) nfParam.getValue();
					statement.setBinaryStream(cursor, inputStream);
				}else{
					statement.setString(cursor,nfParam.getStringValue());
				}
			} else {
				String paramValue = nfParam.getStringValue();
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
		String paramValue = "";

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

			// isIsCollection 逻辑支持 2022年07月13日11:21:39
			if("collection".equals(nfParam.getDataType())){
				Object object = nfParam.getValue();
				int length  = DataUtil.getDataLength(object);
				if(length < 1){
					throw new DBFoundRuntimeException("collection param, data size must > 1");
				}
				Map<String,Object> root = new HashMap<>();
				for(int i=0; i < length ; i++){
					StringBuilder buffer = new StringBuilder();
					root.put("data",object);
					buffer.append("data[").append(i).append("].").append(nfParam.getInnerPath());

					Object value = DBFoundEL.getData(buffer.toString(),root);
					if(value instanceof Enum) {
						value = getEnumValue((Enum) value);
					}if(value instanceof  java.sql.Date){
						SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateFormat());
						value = format.format(value);
					}else if (value instanceof java.util.Date) {
						SimpleDateFormat format = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
						value = format.format(value);
					} else {
						value = value.toString();
					}

					if(i==0){
						paramValue = paramValue + value;
					}else{
						paramValue = paramValue + "," + value;
					}
				}
			}else{
				paramValue = nfParam.getStringValue();
			}

			// UUID取值
			if (nfParam.isUUID()) {
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
		if(nfParam.getValue() instanceof Enum){
			Object value = getEnumValue((Enum) nfParam.getValue());
			nfParam.setValue(value);
		}

		if("boolean".equals(nfParam.getDataType())){
			if( !(nfParam.getValue() instanceof Boolean) && DataUtil.isNotNull(nfParam.getValue()) ) {
				if ("false".equals(nfParam.getValue()) || "0".equals(nfParam.getStringValue())) {
					nfParam.setValue(false);
				} else {
					nfParam.setValue(true);
				}
			}
		} else if("number".equals(nfParam.getDataType()) ){
			if( nfParam.getValue() instanceof Boolean) {
				if ((Boolean) nfParam.getValue()) {
					nfParam.setValue(1);
				} else {
					nfParam.setValue(0);
				}
			}
		}
	}

	private void initParamType(Param nfParam){
		if ("unknown".equals(nfParam.getDataType())){
			Object value = nfParam.getValue();
			if (value != null){

				if (value instanceof String){
					nfParam.setDataType("varchar");
				} else if (value instanceof Number){
					nfParam.setDataType("number");
				} else if (value instanceof Date){
					nfParam.setDataType("date");
				} else if (value instanceof Boolean){
					nfParam.setDataType("boolean");
				} else if (value instanceof List || value instanceof Set || value instanceof MysqlxExpr.Object[]) {
					nfParam.setDataType("collection");
				} else if (value instanceof InputStream){
					nfParam.setDataType("file");
				} else {
					nfParam.setDataType("varchar");
				}
			}
		}
	}

	private Object getEnumValue(Enum object){
		EnumTypeHandler handler = EnumHandlerFactory.getEnumHandler(object.getClass());
		Object value = handler.getEnumValue(object);
		return value;
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
