package com.nfwork.dbfound.model.bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.dto.FileDownloadResponseObject;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.*;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.enums.EnumTypeHandler;
import com.nfwork.dbfound.util.*;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.web.file.FilePart;

public abstract class SqlEntity extends Entity {
	private final static String paramReplace = "\\{@[ a-zA-Z_0-9\u4E00-\u9FA5]*}";

	protected final static Pattern dynamicPattern = Pattern.compile("\\$" + paramReplace);

	protected final static Pattern staticPattern = Pattern.compile("#" + paramReplace);

	protected final static Pattern paramPattern = Pattern.compile(paramReplace);

	protected final static Pattern SQL_PART_PATTERN  = Pattern.compile("#[A-Z_]+#");
	protected static final String SQL_PART = "#SQL_PART#";

	protected final static Pattern timeMillisPattern = Pattern.compile("[0123456789]*");

	@Override
	public void doEndTag() {
		Entity entity = getParent();
		if (entity instanceof Sqls) {
			Sqls sqls = (Sqls) entity;
			sqls.sqlList.add(this);
		}
	}

	public abstract void execute(Context context, Map<String, Param> params, String provideName);

	/**
	 * 得到最后执行的sql语句
	 * 
	 * @return string
	 */
	protected String getExecuteSql(String sql, Map<String, Param> params, List<Object> exeParam) {

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
			initParamValue(nfParam);
			initParamType(nfParam);

			if(nfParam.getDataType() == DataType.COLLECTION){
				initCollection(nfParam);
				SimpleItemList itemList = (SimpleItemList) nfParam.getValue();

				StringBuilder value = new StringBuilder();
				for(Object item : itemList){
					value.append("?,");
					exeParam.add(item);
				}
				if(value.length()>0){
					value.deleteCharAt(value.length()-1);
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

	protected void initParam(PreparedStatement statement, List<Object> exeParam) throws SQLException {
		try {
			initParam(statement, exeParam, null);
		}catch (IOException exception){
			throw new DBFoundPackageException("init param failed", exception);
		}
	}

		/**
         * 参数设定 sql为原生sql语句，用来寻找参数的位置
         *
         * @throws SQLException sql exception
         * @throws NumberFormatException Number Format Exception
         */
	protected void initParam(PreparedStatement statement, List<Object> exeParam, List<InputStream> files) throws SQLException, IOException {
		Calendar defaultCalendar = Calendar.getInstance();
		int cursor = 1;
		for (Object value : exeParam){
			if(value == null){
				statement.setString(cursor,null);
			}else if(value instanceof String){
				statement.setString(cursor,(String)value);
			}else if(value instanceof Number){
				if(value instanceof Integer){
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
				} else {
					statement.setString(cursor, value.toString());
				}
			}else if(value instanceof Boolean){
				statement.setBoolean(cursor,(Boolean) value);
			} else if (value instanceof Date) {
				if (value instanceof java.sql.Date) {
					statement.setDate(cursor, (java.sql.Date) value, defaultCalendar);
				} else if(value instanceof Time){
					statement.setTime(cursor,(Time) value, defaultCalendar);
				} else if(value instanceof Timestamp){
					statement.setTimestamp(cursor,(Timestamp) value, defaultCalendar);
				} else {
					Date date = (Date) value;
					statement.setTimestamp(cursor, new Timestamp(date.getTime()), defaultCalendar);
				}
			} else if (value instanceof Temporal) {
				if(value instanceof LocalDate){
					java.sql.Date date =  java.sql.Date.valueOf((LocalDate) value);
					statement.setDate(cursor, date, defaultCalendar);
				}else if(value instanceof LocalTime){
					Time time = Time.valueOf((LocalTime) value);
					statement.setTime(cursor, time, defaultCalendar);
				}else if(value instanceof LocalDateTime){
					Timestamp timestamp = Timestamp.valueOf((LocalDateTime) value);
					statement.setTimestamp(cursor, timestamp, defaultCalendar);
				}else {
					statement.setString(cursor, value.toString());
				}
			} else if (value instanceof FilePart) {
				if(files != null){
					InputStream inputStream = ((FilePart) value).inputStream();
					statement.setBinaryStream(cursor, inputStream);
					files.add(inputStream);
				}else{
					throw new DBFoundRuntimeException("the file param can be only used in ExecuteSql");
				}
			} else if (value instanceof File) {
				if(files != null){
					File file = (File)value;
					InputStream inputStream = Files.newInputStream(file.toPath());
					statement.setBinaryStream(cursor, inputStream);
					files.add(inputStream);
				}else{
					throw new DBFoundRuntimeException("the file param can be only used in ExecuteSql");
				}
			} else if (value instanceof InputStream) {
				if(files != null){
					statement.setBinaryStream(cursor, (InputStream) value);
					files.add((InputStream) value);
				}else{
					throw new DBFoundRuntimeException("the file param only can be only used in ExecuteSql");
				}
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
	public String staticParamParse(String sql, Map<String, Param> params) {
		if (sql == null || sql.isEmpty()) {
			return "";
		}
		int findCount = 0;
		Matcher m = staticPattern.matcher(sql);
		StringBuffer buf = new StringBuffer();

		while (m.find()) {
			findCount++;
			String paramValue;

			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " not defined");
			}

			nfParam.setRequireLog(true);
			initParamValue(nfParam);
			initParamType(nfParam);

			if(nfParam.getDataType() == DataType.COLLECTION){

				initCollection(nfParam);
				SimpleItemList itemList = (SimpleItemList) nfParam.getValue();

				StringBuilder paramBuilder = new StringBuilder();

				for(Object item : itemList){
					if(item == null){
						continue;
					}
					String value;
					if (item instanceof Date) {
						value = LocalDateUtil.formatDate((Date) item);
					} else if(item instanceof Temporal) {
						value = LocalDateUtil.formatTemporal((Temporal) item);
					} else {
						value = item.toString();
					}
					paramBuilder.append(value).append("," );
				}
				if(paramBuilder.length()>0){
					paramBuilder.deleteCharAt(paramBuilder.length()-1);
				}
				paramValue = paramBuilder.toString();
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
			reduceBlank(buf);
		}
		if(findCount == 0){
			return sql;
		}else {
			m.appendTail(buf);
			return buf.toString();
		}
	}

	protected void reduceBlank(StringBuffer buffer){
		if(buffer.length() == 0){
			return;
		}
		int blankCount = 0;
		int index = buffer.length() - 1;
		while(index >=0 && buffer.charAt(index)==' '){
			blankCount ++;
			index--;
		}
		if(blankCount > 0){
			buffer.delete(buffer.length()- blankCount, buffer.length());
		}
	}

	/**
	 * 枚举类型 boolean类型支持 2022年07月08日17:26:06
	 * @param nfParam param
	 */
	protected void initParamValue(Param nfParam){

		if(nfParam.getDataType() == null){
			throw new DBFoundRuntimeException("dataType can not be null, it only can be one of varchar, number, boolean, date, file or collection");
		}

		if(nfParam.getValue() instanceof Enum){
			Object value = getEnumValue((Enum<?>) nfParam.getValue());
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
					String paramValue = JsonUtil.toJson(nfParam.getValue());
					nfParam.setValue(paramValue);
				} else if (nfParam.getValue() instanceof Collection) {
					String paramValue = JsonUtil.toJson( nfParam.getValue());
					nfParam.setValue(paramValue);
				} else if (nfParam.getValue().getClass().isArray()) {
					String paramValue = JsonUtil.toJson(nfParam.getValue());
					nfParam.setValue(paramValue);
				} else{
					nfParam.setValue(nfParam.getStringValue());
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
							nfParam.setValue(LocalDateUtil.parseDate(paramValue));
						} catch (Exception exception) {
							throw new DBFoundRuntimeException("parse date exception, value :" + paramValue, exception);
						}
					} else if (paramValue.length() == DBFoundConfig.getDateTimeFormat().length()) {
						try {
							nfParam.setValue(LocalDateUtil.parseDateTime(paramValue));
						} catch (Exception exception) {
							throw new DBFoundRuntimeException("parse datetime exception, value :" + paramValue, exception);
						}
					} else if (paramValue.length() == DBFoundConfig.getTimeFormat().length()) {
						try {
							nfParam.setValue(LocalDateUtil.parseTime(paramValue));
						} catch (Exception exception) {
							throw new DBFoundRuntimeException("parse time exception, value :" + paramValue, exception);
						}
					}
				} else {
					throw new DBFoundRuntimeException("can not cost "+ nfParam.getValue().getClass() +" to date");
				}
			}
		}else if (nfParam.getDataType() == DataType.FILE)  {
			if (nfParam.getValue() instanceof String) {
				File file = new File((String) nfParam.getValue());
				nfParam.setValue(file);
			}
		}
	}

	protected void initParamType(Param nfParam){
		if (nfParam.getDataType() == DataType.UNKNOWN){
			Object value = nfParam.getValue();
			if (value != null){
				if (value instanceof String){
					nfParam.setDataType(DataType.VARCHAR);
				} else if (value instanceof Number){
					nfParam.setDataType(DataType.NUMBER);
				} else if (value instanceof Date || value instanceof Temporal){
					nfParam.setDataType(DataType.DATE);
				} else if (value instanceof Boolean){
					nfParam.setDataType(DataType.BOOLEAN);
				} else if (value instanceof FilePart || value instanceof File || value instanceof InputStream || value instanceof byte[]){
					nfParam.setDataType(DataType.FILE);
				} else if (value instanceof Collection || DataUtil.isArray(value)) {
					nfParam.setDataType(DataType.COLLECTION);
				} else {
					nfParam.setDataType(DataType.VARCHAR);
				}
			}
		}
	}

	private void initCollection(Param nfParam){
		Object value = nfParam.getValue();
		if (!(value instanceof SimpleItemList)){
			if(value instanceof String){
				value = Arrays.stream(((String) value).split(",")).map(String::trim).collect(Collectors.toList());
			}
			int length = DataUtil.getDataLength(value);
			if (length == 0 || value == null) {
				throw new DBFoundRuntimeException("collection param data size must >= 1, param name: "+ nfParam.getName());
			}else if(length == -1){
				throw new DBFoundRuntimeException("can not convert ‘" + value.getClass() + "’ to a collection, param name: " + nfParam.getName() +", param value: " + value);
			}
			SimpleItemList itemList = new SimpleItemList(length);

			//el处理非arrayList集合性能较差，转化为array
			if(!(value instanceof ArrayList) && value instanceof Collection){
				value = ((Collection<?>)value).toArray();
			}

			for (int i = 0; i < length; i++) {
				Object pValue = DBFoundEL.getDataByIndex(i, value);
				if(DataUtil.isNotNull(nfParam.getInnerPath())){
					pValue = DBFoundEL.getData(nfParam.getInnerPath(),pValue);
				}

				if (pValue instanceof Enum) {
					pValue = getEnumValue((Enum<?>) pValue);
				}
				itemList.add(pValue);
			}
			nfParam.setValue(itemList);
		}
	}

	private Object getEnumValue(Enum<?> object){
		EnumTypeHandler<Enum<?>> handler = EnumHandlerFactory.getEnumHandler(object.getClass());
		return handler.getEnumValue(object);
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
				if(colName.contains("_")) {
					colName = underscoreToCamelCase(colName);
				}
			}
			colNames[i-1] = colName;
		}
		return  colNames;
	}

	public Object getData(int columnType, ResultSet dataset, int index, Calendar defaultCalendar) throws SQLException {
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
				result = dataset.getFloat(index);
				break;
			case Types.DOUBLE:
			case Types.DECIMAL:
			case Types.NUMERIC:
				result = dataset.getDouble(index);
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
				result = dataset.getString(index);
		}
		return result;
	}

	protected Boolean checkCondition(String condition,  Map<String, Param> params, Context context, String provideName){
		String conditionSql = staticParamParse(condition, params);
		List<Object> exeParam = new ArrayList<>();
		conditionSql = getExecuteSql(conditionSql, params, exeParam);
        return DSqlEngine.checkWhenSql(conditionSql, exeParam, provideName, context);
	}

	protected String getSqlTask(Context context, String name){
		Entity parent = this;
		String entityName = null;

		while (parent != null) {
			if (parent instanceof Query) {
				entityName = ((Query) parent).getName();
				break;
			}
			if (parent instanceof Execute) {
				entityName = ((Execute) parent).getName();
				break;
			}
			parent = parent.getParent();
		}

		return context.getCurrentModel() + "#" + entityName +"#"+name;
	}

	/**
	 * 处理参数 放入session、cookie、或者以outParam返回
	 *
	 * @param context context
	 * @param params params
	 * @return Map
	 */
	protected ResponseObject initOutParams(Context context, Map<String, Param> params, ResponseObject responseObject) {
		boolean inWeb = context.request != null && context.response != null;
		for (Param p : params.values()) {
			if (inWeb) {
				// 设定session参数
				if (p.isAutoSession()) {
					context.setSessionData(p.getName(), p.getValue());
				}
				// 设定cookie参数
				if (p.isAutoCookie()) {
					context.addCookie(p);
				}
				// 将out参数输出
				if (p.getIoType() != IOType.IN) {
					if (p.getDataType() == DataType.FILE) {
						if(!context.isExport() && ! (responseObject instanceof QueryResponseObject)) {
							return new FileDownloadResponseObject(p, params);
						}
					} else {
						context.setOutParamData(p.getName(), p.getValue());
					}
				}
			} else {
				// 将out参数输出
				if (p.getIoType() != IOType.IN) {
					context.setOutParamData(p.getName(), p.getValue());
				}
			}
		}
		responseObject.setOutParam(context.getOutParamDatas());
		return responseObject;
	}

	protected void setParam(Param nfParam, Context context, String cp, Map<String, Object> elCache) {

		// 增加UUID取值 在sql执行的时候动态的获取UUID 2012年8月8日8:47:08
		if (nfParam.isUUID()) {
			nfParam.setSourcePathHistory("UUID");
			nfParam.setDataType(DataType.VARCHAR);
			return;
		}
		// end 修改

		if (nfParam.getIoType() == IOType.OUT) {
			return; // out参数直接返回
		}

		// dbfound1.3新功能，根据sourcePath到pagerContext容器中取数据
		String scope = nfParam.getScope();

		// 设置 当前取值路径
		String currentPath;
		if (DataUtil.isNotNull(scope)) {
			currentPath = scope;
		} else {
			currentPath = cp;
		}

		String realPath ;// 绝对路径

		// 得到取值的相对路径
		String sourcePath = nfParam.getSourcePath();
		if (DataUtil.isNull(sourcePath)) {
			sourcePath = nfParam.getName();
			realPath = currentPath + "." + sourcePath;
		} else {
			// 判断sourcePath 是绝对路径，还是相当路径
			if (ELEngine.isAbsolutePath(sourcePath)) {
				realPath = sourcePath;
			} else {
				realPath = currentPath + "." + sourcePath;
			}
		}

		// 取值
		Object paramValue = context.getData(realPath, elCache);

		if(paramValue != null) {
			if ("".equals(paramValue)) {
				if(!nfParam.isEmptyAsNull()) {
					nfParam.setValue("");
				}
			} else{
				nfParam.setValue(paramValue);
			}
		}
		nfParam.setSourcePathHistory(realPath);
	}

	public void log(String sqlName, String sql, Map<String, Param> params) {
		LogUtil.log(sqlName, sql, params.values());
	}

}
