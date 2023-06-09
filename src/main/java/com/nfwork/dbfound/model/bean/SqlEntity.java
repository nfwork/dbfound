package com.nfwork.dbfound.model.bean;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.base.SimpleItemList;
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

	private final static String paramReplace = "\\{@[ a-zA-Z_0-9\u4E00-\u9FA5]*}";

	protected final static Pattern dynamicPattern = Pattern.compile("\\$" + paramReplace);

	protected final static Pattern staticPattern = Pattern.compile("#" + paramReplace);

	protected final static Pattern paramPattern = Pattern.compile(paramReplace);

	protected final static Pattern timeMillisPattern = Pattern.compile("[0123456789]*");

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
	 * @return string
	 */
	public String getExecuteSql(String sql, Map<String, Param> params, List<Object> exeParam, Context context) {

		Matcher m = dynamicPattern.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " not defined");
			}
			if (nfParam.isUUID()) {
				nfParam.setValue(UUIDUtil.getUUID());
			}

			nfParam.setRequireLog(true);
			initParamValue(nfParam, context);
			initParamType(nfParam);

			if(nfParam.getDataType() == DataType.COLLECTION){
				initCollection(nfParam);
				SimpleItemList itemList = (SimpleItemList) nfParam.getValue();

				StringBuilder value = new StringBuilder();
				boolean isFirst = true;
				for(Object item : itemList){
					if(isFirst){
						value.append("?");
						isFirst = false;
					}else{
						value.append(", ?");
					}
					exeParam.add(item);
				}
				m.appendReplacement(buf, value.toString());
			}else{
				exeParam.add(nfParam.getValue());
				m.appendReplacement(buf, "?");
			}
		}
		m.appendTail(buf);
		return buf.toString();
	}

	/**
	 * 自动补齐 Param定义
	 * since 2.5.0
	 */
	public void autoCreateParam(String sql, Map<String, Param> params) {
		Matcher m = paramPattern.matcher(sql);
		while (m.find()) {
			String paramName = m.group();
			String name = paramName.substring(2, paramName.length() - 1).trim();
			if(params.get(name)==null) {
				Param nfParam = new Param();
				nfParam.setName(name);
				nfParam.setDataType(DataType.UNKNOWN);
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
	 * @throws SQLException sql exception
	 * @throws NumberFormatException Number Format Exception
	 */
	public void initParam(PreparedStatement statement, List<Object> exeParam)throws SQLException {
		int cursor = 1;
		for (Object value : exeParam){
			if(value == null){
				statement.setString(cursor,null);
			}else if(value instanceof String){
				statement.setString(cursor,(String)value);
			} else if(value instanceof Integer){
				statement.setInt(cursor,(Integer)value);
			} else if(value instanceof Long){
				statement.setLong(cursor,(Long) value);
			} else if(value instanceof Double){
				statement.setDouble(cursor,(Double) value);
			} else if(value instanceof Float){
				statement.setFloat(cursor,(Float) value);
			} else if(value instanceof Short){
				statement.setShort(cursor,(Short) value);
			} else if(value instanceof BigDecimal){
				statement.setBigDecimal(cursor,(BigDecimal) value);
			} else if(value instanceof Byte){
				statement.setByte(cursor,(Byte) value);
			} else if(value instanceof Boolean){
				statement.setBoolean(cursor,(Boolean) value);
			} else if (value instanceof java.sql.Date) {
				java.sql.Date date = (java.sql.Date) value;
				statement.setDate(cursor, date);
			} else if (value instanceof Date) {
				Date date = (Date) value;
				statement.setTimestamp(cursor, new Timestamp(date.getTime()));
			} else if (value instanceof InputStream) {
				statement.setBinaryStream(cursor, (InputStream) value);
			} else if (value instanceof byte[]) {
				statement.setBytes(cursor, (byte[]) value);
			} else{
				statement.setString(cursor,value.toString());
			}
			cursor++;
		}
	}

	/**
	 * 静态参数 初始化
	 * @param sql sql
	 * @param params params
	 * @return string
	 */
	public String staticParamParse(String sql, Map<String, Param> params, Context context) {
		if (sql == null || "".equals(sql)) {
			return "";
		}
		Matcher m = staticPattern.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String paramValue = "";

			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " not defined");
			}

			nfParam.setRequireLog(true);
			initParamValue(nfParam, context);
			initParamType(nfParam);

			if(nfParam.getDataType() == DataType.COLLECTION){

				initCollection(nfParam);
				SimpleItemList itemList = (SimpleItemList) nfParam.getValue();

				StringBuilder paramBuilder = new StringBuilder();

				boolean isFirst = true;
				for(Object item : itemList){
					if(item == null){
						continue;
					}

					String value;
					if(item instanceof  java.sql.Date){
						SimpleDateFormat format = context.getDateFormat();
						value = format.format(item);
					}else if (item instanceof Date) {
						SimpleDateFormat format = context.getDateTimeFormat();
						value = format.format(item);
					} else {
						value = item.toString();
					}

					if(isFirst){
						paramBuilder.append(value);
						isFirst = false;
					}else{
						paramBuilder.append(", " ).append(value);
					}
				}
				paramValue = paramBuilder.toString();
			}else{
				paramValue = nfParam.getStringValue(context);
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
	 * @param nfParam param
	 */
	private void initParamValue(Param nfParam, Context context){

		if(nfParam.getDataType() == null){
			throw new DBFoundRuntimeException("dataType can not be null, it only can be one of varchar, number, boolean, date, file or collection");
		}

		if(nfParam.getValue() instanceof Enum){
			Object value = getEnumValue((Enum) nfParam.getValue());
			nfParam.setValue(value);
		}

		if(nfParam.getValue() == null){
			return;
		}

		if(nfParam.getDataType() == DataType.BOOLEAN){
			if( !(nfParam.getValue() instanceof Boolean)) {
				String paramValue = nfParam.getValue().toString().trim();
				if ("".equals(paramValue)) {
					nfParam.setValue(null);
				}else if ("false".equals(paramValue) || "0".equals(paramValue)) {
					nfParam.setValue(false);
				} else {
					nfParam.setValue(true);
				}
			}
		} else if(nfParam.getDataType() == DataType.NUMBER ){
			if( !(nfParam.getValue() instanceof Number)) {
				if (nfParam.getValue() instanceof Boolean) {
					if ((Boolean) nfParam.getValue()) {
						nfParam.setValue(1);
					} else {
						nfParam.setValue(0);
					}
				} else if (nfParam.getValue() instanceof String) {
					String paramValue = nfParam.getValue().toString().trim();
					if ("".equals(paramValue)) {
						nfParam.setValue(null);
					} else if (!paramValue.contains(".")) {
						nfParam.setValue(Long.parseLong(paramValue));
					} else if (paramValue.endsWith(".0")) {
						paramValue = paramValue.substring(0, paramValue.length() - 2);
						nfParam.setValue(Long.parseLong(paramValue));
					} else {
						nfParam.setValue(Double.parseDouble(paramValue));
					}
				} else {
					throw new DBFoundRuntimeException("can not cost "+ nfParam.getValue().getClass() +" to number");
				}
			}
		}else if (nfParam.getDataType() == DataType.VARCHAR) {
			if(!(nfParam.getValue() instanceof String)) {
				if (nfParam.getValue() instanceof Map) {
					String paramValue = JsonUtil.mapToJson((Map) nfParam.getValue(), context);
					nfParam.setValue(paramValue);
				} else if (nfParam.getValue() instanceof Set) {
					String paramValue = JsonUtil.setToJson((Set) nfParam.getValue(), context);
					nfParam.setValue(paramValue);
				} else if (nfParam.getValue() instanceof List) {
					String paramValue = JsonUtil.listToJson((List) nfParam.getValue(), context);
					nfParam.setValue(paramValue);
				} else if (nfParam.getValue() instanceof Object[]) {
					String paramValue = JsonUtil.arrayToJson((Object[]) nfParam.getValue(), context);
					nfParam.setValue(paramValue);
				} else{
					nfParam.setValue(nfParam.getStringValue(context));
				}
			}
		} else if (nfParam.getDataType() == DataType.DATE) {
			if (!(nfParam.getValue() instanceof Date) && !(nfParam.getValue() instanceof Temporal)) {
				if(nfParam.getValue() instanceof Long){
					nfParam.setValue(new Timestamp((Long) nfParam.getValue()));
				} else if(nfParam.getValue() instanceof String){
					String paramValue = nfParam.getValue().toString().trim();
					if (timeMillisPattern.matcher(paramValue).matches()) {
						nfParam.setValue(new Timestamp(Long.parseLong(paramValue)));
					} else if (paramValue.length() == DBFoundConfig.getDateFormat().length()) {
						try {
							SimpleDateFormat dateFormat = context.getDateFormat();
							nfParam.setValue(new java.sql.Date(dateFormat.parse(paramValue).getTime()));
						} catch (ParseException exception) {
							throw new DBFoundRuntimeException("parse date exception, value :" + paramValue, exception);
						}
					} else if (paramValue.length() == DBFoundConfig.getDateTimeFormat().length()) {
						try {
							SimpleDateFormat dateTimeFormat = context.getDateTimeFormat();
							nfParam.setValue(dateTimeFormat.parse(paramValue));
						} catch (ParseException exception) {
							throw new DBFoundRuntimeException("parse datetime exception, value :" + paramValue, exception);
						}
					}
				} else {
					throw new DBFoundRuntimeException("can not cost "+ nfParam.getValue().getClass() +" to date");
				}
			}
		}
	}

	private void initParamType(Param nfParam){
		if (nfParam.getDataType() == DataType.UNKNOWN){
			Object value = nfParam.getValue();
			if (value != null){
				if (value instanceof String){
					nfParam.setDataType(DataType.VARCHAR);
				} else if (value instanceof Number){
					nfParam.setDataType(DataType.NUMBER);
				} else if (value instanceof Date){
					nfParam.setDataType(DataType.DATE);
				} else if (value instanceof Boolean){
					nfParam.setDataType(DataType.BOOLEAN);
				} else if (value instanceof List || value instanceof Set || value instanceof Object[]
						|| value instanceof int[] || value instanceof long[] ||value instanceof double[] ||value instanceof float[] ) {
					nfParam.setDataType(DataType.COLLECTION);
				} else if (value instanceof InputStream || value instanceof byte[]){
					nfParam.setDataType(DataType.FILE);
				} else {
					nfParam.setDataType(DataType.VARCHAR);
				}
			}
		}
	}

	private void initCollection(Param nfParam){
		if (!(nfParam.getValue() instanceof SimpleItemList)){
			int length = DataUtil.getDataLength(nfParam.getValue());
			if (length < 1) {
				throw new DBFoundRuntimeException("collection param, data size must >= 1");
			}
			SimpleItemList itemList = new SimpleItemList(length);

			for (int i = 0; i < length; i++) {
				Object pValue = DBFoundEL.getDataByIndex(i, nfParam.getValue());
				if(DataUtil.isNotNull(nfParam.getInnerPath())){
					pValue = DBFoundEL.getData(nfParam.getInnerPath(),pValue);
				}

				if (pValue instanceof Enum) {
					pValue = getEnumValue((Enum) pValue);
				}
				itemList.add(pValue);
			}
			nfParam.setValue(itemList);
		}
	}

	private Object getEnumValue(Enum object){
		EnumTypeHandler handler = EnumHandlerFactory.getEnumHandler(object.getClass());
		Object value = handler.getEnumValue(object);
		return value;
	}

	public String[] getColNames(ResultSetMetaData metaset) throws SQLException {
		int size = metaset.getColumnCount();
		String[] colNames = new String[size];
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
			case Types.DATE:
				result = dataset.getDate(index, defaultCalendar);
				break;
			case Types.TIME:
				result = dataset.getTime(index, defaultCalendar);
				break;
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

	public void log(String sqlName, String sql, Map<String, Param> params, Context context) {
		LogUtil.log(sqlName, sql, params.values(), context);
	}

	public void log(String sqlName,String sql, List<Param> listParam, Context context) {
		LogUtil.log(sqlName, sql, listParam, context);
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
