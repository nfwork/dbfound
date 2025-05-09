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

	protected final static Pattern executeParamPattern = Pattern.compile("[#$]" + paramReplace);

	protected final static Pattern staticPattern = Pattern.compile("#" + paramReplace);

	protected final static Pattern paramPattern = Pattern.compile(paramReplace);

	protected final static Pattern KEY_PART_PATTERN  = Pattern.compile("#[A-Z_]+#");

	protected static final String SQL_PART = "#SQL_PART#";
	protected final static Pattern SQL_PART_PATTERN  = Pattern.compile(SQL_PART);

	protected final static Pattern timeMillisPattern = Pattern.compile("[0-9]+");

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
		StringBuilder builder = new StringBuilder();
		initExecuteSql(sql, params, exeParam, builder);
		return builder.toString();
	}

	protected void initExecuteSql(String sql, Map<String, Param> params, List<Object> exeParam, StringBuilder builder) {
		Matcher m = executeParamPattern.matcher(sql);
		int start = 0;

		while (m.find()) {
			if(m.start() > start){
				builder.append(sql, start, m.start());
			}
			start = m.end();
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

			if(param.charAt(0)=='$') {
				if (nfParam.getDataType() == DataType.COLLECTION) {
					initCollection(nfParam);
					SimpleItemList itemList = (SimpleItemList) nfParam.getValue();
					Iterator<Object> iterator = itemList.iterator();
					if (iterator.hasNext()){
						builder.append("?");
						exeParam.add(iterator.next());
						while (iterator.hasNext()){
							builder.append(",?");
							exeParam.add(iterator.next());
						}
					}
				} else {
					exeParam.add(nfParam.getValue());
					builder.append("?");
				}
			}else{
				if (nfParam.getDataType() == DataType.COLLECTION) {
					initCollection(nfParam);
					SimpleItemList itemList = (SimpleItemList) nfParam.getValue();
					Iterator<String> iterator = itemList.stream().filter(Objects::nonNull).map(this::parseCollectionParamItem).iterator();
					if (iterator.hasNext()){
						builder.append(iterator.next());
						while(iterator.hasNext()){
							builder.append(",").append(iterator.next());
						}
					}
				} else {
					String value = nfParam.getStringValue();
					if(value == null){
						value = "";
					}
					// 判断静态传参内容，是否包含动态参数
					if(nfParam.getDataType() == DataType.VARCHAR && value.contains("${@")){
						initExecuteSql(value, params, exeParam, builder);
					}else {
						builder.append(value);
					}
				}
			}
		}
		if(start < sql.length()) {
			builder.append(sql, start, sql.length());
		}
	}

	/**
	 * 自动补齐 Param定义
	 * since 2.5.0
	 */
	protected void autoCreateParam(String sql, Map<String, Param> params, Map<String, Param> globalParams) {
		Matcher m = paramPattern.matcher(sql);
		while (m.find()) {
			String paramName = m.group();
			String name = paramName.substring(2, paramName.length() - 1).trim();
			if(params.get(name)==null && globalParams.get(name)==null) {
				Param nfParam = new Param();
				nfParam.setName(name);
				nfParam.setDataType(DataType.UNKNOWN);
				params.put(name, nfParam);
			}
		}
	}

	protected void autoCreateParam(String sql, Entity entity) {
		if (DataUtil.isNull(sql))return;
		Entity entityParent = entity.getParent();
		while (entityParent != null){
			if ( entityParent instanceof  Execute){
				Execute execute = (Execute) entityParent;
				Model model = (Model) execute.getParent();
				autoCreateParam(sql, execute.getParams(), model.getParams());
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
		int start = 0;
		Matcher m = staticPattern.matcher(sql);
		StringBuilder buf = new StringBuilder();

		while (m.find()) {
			if(m.start() > start){
				buf.append(sql, start, m.start());
			}
			start = m.end();
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
				Iterator<String> iterator = itemList.stream().filter(Objects::nonNull).map(this::parseCollectionParamItem).iterator();
				if (iterator.hasNext()){
					paramBuilder.append(iterator.next());
					while(iterator.hasNext()) {
						paramBuilder.append(",").append(iterator.next());
					}
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
			}
			if(paramValue.isEmpty()) {
				reduceBlank(buf);
			}else{
				buf.append(paramValue);
			}
		}
		if(start == 0){
			return sql;
		}else {
			if(start < sql.length()) {
				buf.append(sql, start, sql.length());
			}
			return buf.toString();
		}
	}

	private String parseCollectionParamItem(Object value){
		String result;
		if (value instanceof Date) {
			result = LocalDateUtil.formatDate((Date) value);
		} else if(value instanceof Temporal) {
			result = LocalDateUtil.formatTemporal((Temporal) value);
		} else {
			result = value.toString();
		}
		return result;
	}

	protected void reduceBlank(StringBuilder builder){
		if(builder.length() == 0){
			return;
		}
		int blankCount = 0;
		int index = builder.length() - 1;
		while(index >=0 && builder.charAt(index)==' '){
			blankCount ++;
			index--;
		}
		if(blankCount > 0){
			builder.delete(builder.length()- blankCount, builder.length());
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
				if (paramValue.isEmpty()) {
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
					if (paramValue.isEmpty()) {
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
				} else if (DataUtil.isArray(nfParam.getValue())) {
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
				nfParam.setValue(new SimpleItemList());
				return;
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
		List<Object> exeParam = new ArrayList<>();
		String conditionSql = getExecuteSql(condition, params, exeParam);
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

	protected void setParam(Param nfParam, Context context, String currentPath,Object currentData, Map<String, Object> elCache) {
		if (nfParam.isUUID()) {
			nfParam.setSourcePathHistory("UUID");
			nfParam.setDataType(DataType.VARCHAR);
			return;
		}
		if (nfParam.getIoType() == IOType.OUT) {
			return;
		}
		String sph;
		Object value;

		if (DataUtil.isNotNull(nfParam.getScope())) {
			Map<?,?> data = (Map<?,?>)context.getDatas().get(nfParam.getScope());
			if(data !=null) {
				value = data.get(nfParam.getName());
			}else{
				value = null;
			}
			sph = nfParam.getScope() +"."+nfParam.getName();
		}else {
			String sourcePath = nfParam.getSourcePath();
			if (DataUtil.isNull(sourcePath)) {
				sourcePath = nfParam.getName();
				sph = currentPath + "." + sourcePath;
				value = DBFoundEL.getData(sourcePath, currentData);
			} else {
				if (ELEngine.isAbsolutePath(sourcePath)) {
					sph = sourcePath;
					value = context.getData(sph, elCache);
				} else {
					sph = currentPath + "." + sourcePath;
					value = DBFoundEL.getData(sourcePath, currentData);
				}
			}
		}
		if ("".equals(value) && nfParam.isEmptyAsNull()) {
			value = null;
		}
		if(value != null) {
			nfParam.setValue(value);
		}
		nfParam.setSourcePathHistory(sph);
	}

	public void log(String sqlName, String sql, Map<String, Param> params, List<Object> exeParam) {
		LogUtil.logSql(sqlName, sql, params.values(),exeParam);
	}

}
