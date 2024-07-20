package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.nfwork.dbfound.db.dialect.AbstractSqlDialect;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.SqlExecuteException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.model.adapter.AdapterFactory;
import com.nfwork.dbfound.model.adapter.ObjectQueryAdapter;
import com.nfwork.dbfound.model.adapter.QueryAdapter;
import com.nfwork.dbfound.model.base.Count;
import com.nfwork.dbfound.model.base.CountType;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.resolver.TypeResolverTool;
import com.nfwork.dbfound.util.*;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.reflector.ReflectorUtil;

public class Query extends SqlEntity {
	private String connectionProvide;
	private String name = "_default"; // query对象的名字
	private Map<String, Param> params; // query对象对应参数
	private Map<String, Filter> filters;
	private String rootPath;
	private String modelName;
	private Integer queryTimeout;
	private static final String WHERE_CLAUSE = "#WHERE_CLAUSE#";
	private static final String AND_CLAUSE = "#AND_CLAUSE#";
	private static final char[] FROM = "from".toCharArray();
	private static final char[] ORDER = "order".toCharArray();
	private static final char[] DISTINCT = "distinct".toCharArray();
	private static final char[] UNION = "union".toCharArray();
	private static final char[] GROUP = "group".toCharArray();

	private Integer pagerSize;
	private Integer maxPagerSize = 10000;
	private Integer exportSize = 50 * 10000;
	private String adapter;
	private QueryAdapter queryAdapter;
	private String entity;
	private Class entityClass;
	private String currentPath;
	private Sql sql;

	@Override
	public void doStartTag(Element element) {
		params = new LinkedHashMap<>();
		filters = new LinkedHashMap<>();
		super.doStartTag(element);
	}

	@Override
	public void doEndTag() {
		if(DataUtil.isNotNull(adapter)){
			try {
				queryAdapter = AdapterFactory.getQueryAdapter( Class.forName(adapter));
			}catch (Exception exception){
				String message = "queryAdapter init failed, please check the class "+ adapter+" is exists or it is implement QueryAdapter";
				throw new DBFoundRuntimeException(message,exception);
			}
		}
		if(DataUtil.isNotNull(entity)){
			try {
				entityClass = Class.forName(entity);
			}catch (Exception exception){
				String message = "entity init failed";
				throw new DBFoundPackageException(message,exception);
			}
		}

		if(queryAdapter !=null && !(queryAdapter instanceof ObjectQueryAdapter)) {
			Class qClass = queryAdapter.getEntityClass();
			if (qClass != null) {
				entityClass = qClass;
			}
		}

		if (getParent() instanceof Model) {
			Model model = (Model) getParent();
			if(DataUtil.isNull(name)){
				name = "_default";
			}
			model.putQuery(name, this);
			params.putAll(filters);

			if(DataUtil.isNotNull(sql)) {
				autoCreateParam(sql.getSql(), params);
				if(!sql.getSqlPartList().isEmpty()){
					String tmp = sql.getSqlPartList().stream().map(SqlPart::getPart).collect(Collectors.joining(","));
					autoCreateParam(tmp,params);
				}
			}
			if(!filters.isEmpty()){
				String tmp = filters.values().stream().map(v->v.getCondition()+","+v.getExpress()).collect(Collectors.joining(","));
				autoCreateParam(tmp,params);
			}
		} else {
			super.doEndTag();
		}
	}

	public <T> QueryResponseObject<T> doQuery(Context context, String currentPath, boolean autoPaging, Class<T> clazz){

		// 初始化查询参数param
		Map<String, Object> elCache = new HashMap<>();
		Map<String, Param> params = cloneParams();
		for (Param nfParam : params.values()) {
			setParam(nfParam, context, currentPath, elCache);
		}

		// 设想分页参数
		if (autoPaging) {
			prePager(context);
		}

		if(queryAdapter != null){
			queryAdapter.beforeQuery(context, params);
		}

		String provideName = ((Model)getParent()).getConnectionProvide(context, getConnectionProvide());

		LogUtil.info("Query info (modelName:" + context.getCurrentModel() + ", queryName:" + name + ", provideName:"+provideName+")");

		//获取querySql
		String querySql = getQuerySql(context, params, provideName);

		// 查询数据，返回结果
		List<T> datas = doExecuteQuery(context, querySql, params, provideName, clazz, autoPaging);

		QueryResponseObject<T> ro = new QueryResponseObject<>();
		ro.setDatas(datas);

		if(context.getCountType() == CountType.REQUIRED) {
			int dataSize = datas.size();
			int pSize = context.getPagerSize();
			if (pSize == 0 && pagerSize != null) {
				pSize = pagerSize;
			}
			long start = context.getStartWith();
			if (!autoPaging || pSize == 0 || (pSize > dataSize && start == 0)) {
				ro.setTotalCounts(datas.size());
			} else {
				Count count = getCount(querySql);
				count.setDataSize(dataSize);
				count.setTotalCounts(dataSize);

				if (queryAdapter != null) {
					queryAdapter.beforeCount(context, params, count);
				}

				if (count.isExecuteCount()) {
					countItems(context, count, params, provideName);
				}
				ro.setTotalCounts(count.getTotalCounts());
			}
		}

		ro.setSuccess(true);
		ro.setMessage("success");
		initOutParams(context, params, ro);

		if(queryAdapter != null){
			queryAdapter.afterQuery(context,params,ro);
		}

		return ro;
	}

	private Map<String, Param> cloneParams() {
		HashMap<String, Param> params = new LinkedHashMap<>();
		for(Map.Entry<String,Param> entry : this.params.entrySet()){
			params.put(entry.getKey(), (Param) entry.getValue().cloneEntity());
		}
		return params;
	}

	private String getQuerySql(Context context,Map<String, Param> params, String provideName){
		if(sql == null) {
			throw new DBFoundRuntimeException("query entity must have a sql");
		}
		String querySql = initFilterAndSqlPart(sql.getSql(), params, context, provideName);
		querySql = staticParamParse(querySql, params);
		return querySql;
	}

	private <T> List<T> doExecuteQuery(Context context, String querySql, Map<String, Param> params, String provideName, Class<T> clazz, boolean autoPaging) {
		Connection conn = context.getConn(provideName);

		List<Map> data = new ArrayList<>();
		List<Object> exeParam = new ArrayList<>();

		if(context.isExport()){
			if(exportSize != null && exportSize > -1){
				SqlDialect dialect = context.getConnDialect(provideName);
				if(dialect instanceof AbstractSqlDialect){
					AbstractSqlDialect sqlDialect = (AbstractSqlDialect) dialect;
					querySql = sqlDialect.getPagerSql(querySql, exportSize, 0,params);
				}else{
					querySql = dialect.getPagerSql(querySql, exportSize, 0);
				}
			}
		}else if(autoPaging) {
			if (context.getPagerSize() > 0 || pagerSize != null) {
				int ps = context.getPagerSize() > 0 ? context.getPagerSize() : pagerSize;
				if(maxPagerSize != null && ps > maxPagerSize){
					throw new DBFoundRuntimeException("pager size can not great than " + maxPagerSize);
				}
				SqlDialect dialect = context.getConnDialect(provideName);
				if(dialect instanceof AbstractSqlDialect){
					AbstractSqlDialect sqlDialect = (AbstractSqlDialect) dialect;
					querySql = sqlDialect.getPagerSql(querySql, ps, context.getStartWith(),params);
				}else{
					querySql = dialect.getPagerSql(querySql, ps, context.getStartWith());
				}
			}
		}
		String eSql = getExecuteSql(querySql,params, exeParam);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(eSql);
			if (queryTimeout != null) {
				statement.setQueryTimeout(queryTimeout);
			}
			// 参数设定
			initParam(statement, exeParam);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();

			// 如果对象不为null，利用反射构建object对象
			if(clazz == null && entityClass != null){
				clazz = entityClass;
			}
			if (clazz != null && ! Map.class.isAssignableFrom(clazz) && !clazz.equals(Object.class)) {
				if(TypeResolverTool.isSupport(clazz)){
					return ReflectorUtil.parseSimpleList(clazz,dataset);
				}else if(Enum.class.isAssignableFrom(clazz)){
					return ReflectorUtil.parseEnumList(clazz,dataset);
				}else{
					return ReflectorUtil.parseResultList(clazz, dataset);
				}
			}
			String[] colNames = getColNames(metaset);
			Calendar defaultCalendar = Calendar.getInstance();
			while (dataset.next()) {
				Map<String, Object> mapdata = new HashMap<>();
				for (int i = 1; i <= colNames.length; i++) {
					String columnName = colNames[i-1];
					if ("d_rm".equals(columnName)) {// 分页参数 不放入map
						continue;
					}
					if (dataset.getObject(i) == null) {
						mapdata.put(columnName, null);
						continue;
					}
					int columnType = metaset.getColumnType(i);
					mapdata.put(columnName,getData(columnType,dataset,i,defaultCalendar));
				}
				data.add(mapdata);
			}
		} catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"Query"), eSql, e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			LogUtil.log("querySql",eSql, params.values());
		}
		return (List<T>) data;
	}

	private String initFilterAndSqlPart(String ssql, Map<String, Param> params, Context context, String provideName) {
		StringBuilder builder = new StringBuilder();
		for (Param param : params.values()) {
			if (param instanceof Filter){
				Filter nfFilter = (Filter) param;
				if(nfFilter.isActive()) {
					builder.append(nfFilter.getExpress()).append(" and ");
				}else if(DataUtil.isNotNull(nfFilter.getCondition())) {
					if(checkCondition(nfFilter.getCondition(),params,context,provideName)){
						builder.append(nfFilter.getExpress()).append(" and ");
					}
				}else if(DataUtil.isNotNull(nfFilter.getValue())){
					//集合参数，当length为0时，filter不生效
					if(param.getDataType() == DataType.COLLECTION && DataUtil.getDataLength(nfFilter.getValue()) == 0){
						continue;
					}
					builder.append(nfFilter.getExpress()).append(" and ");
				}
			}
		}
		String fsql = builder.length() > 5 ? builder.substring(0, builder.length() - 5) : null;
		if (fsql != null) {
			fsql = Matcher.quoteReplacement(fsql);
		}
		String whereSql = fsql == null ? "" : "where " + fsql;
		String andSql =  fsql == null ? "" : "and " + fsql;

		int sqlPartIndex = 0;
		int findCount = 0;
		int followType = 0;
		int commaIndex = 0;

		Matcher m = SQL_PART_PATTERN.matcher(ssql);
		StringBuffer buffer = new StringBuffer();
		while (m.find()) {
			String text = m.group();
			findCount++;
			switch (text) {
				case WHERE_CLAUSE:
					m.appendReplacement(buffer, whereSql);
					if (fsql == null) {
						followType = 1;
						reduceBlank(buffer);
					} else {
						followType = 2;
					}
					break;
				case AND_CLAUSE:
					m.appendReplacement(buffer, andSql);
					followType = 2;
					if (fsql == null) {
						reduceBlank(buffer);
					}
					break;
				case SQL_PART:
					SqlPart sqlPart = sql.getSqlPartList().get(sqlPartIndex++);
					String partValue = sqlPart.getPartSql(context,params,provideName);

					if(partValue.isEmpty()) {
						m.appendReplacement(buffer, "");
						reduceBlank(buffer);
					}else{
						if(sqlPart.isAutoCompletion() && followType != 0 ){
							if(followType == 1 ){
								partValue = "where " + partValue;
								followType = 2;
							}else {
								partValue = "and " + partValue;
							}
						}else {
							partValue = partValue.replace(WHERE_CLAUSE, whereSql).replace(AND_CLAUSE, andSql);
						}
						partValue = Matcher.quoteReplacement(partValue);
						m.appendReplacement(buffer, partValue);

						if(sqlPart.isAutoClearComma()){
							commaIndex = buffer.length() - 1;
						}
					}
					break;
			}
		}
		if(findCount == 0){
			return ssql;
		}else {
			if(commaIndex > 0 && buffer.charAt(commaIndex) == ',') {
				buffer.deleteCharAt(commaIndex);
			}
			m.appendTail(buffer);
			return buffer.toString();
		}
	}

	private Count getCount(String querySql){

		Count count = new Count();

		char[] sqlChars = querySql.toCharArray();
		int dyh = 0;
		int syh = 0;
		int kh = 0;
		int from_hold = 0;
		int order_hold = 0;
		int group_hold = 0;
		int distinct_hold = 0;
		int union_hold = 0;

		// 寻找from的位置
		for (int i = 6; i < sqlChars.length - 6; i++) {
			if (sqlChars[i] == '(' && dyh == 0  && syh ==0) {
				kh++;
			} else if (sqlChars[i] == ')' && dyh == 0 && syh == 0) {
				kh--;
			} else if (sqlChars[i] == '\'' && sqlChars[i-1] != '\\' && syh == 0) {
				dyh = dyh ^ 1;
			} else if (sqlChars[i] == '\"' && sqlChars[i-1] != '\\' && dyh == 0) {
				syh = syh ^ 1;
			}
			if (sqlChars[i] == ' ' || sqlChars[i] == '\n' || sqlChars[i] == '\t' || sqlChars[i] == ')' ) {
				if (kh == 0 && dyh == 0 && syh == 0) {
					int index = i + 1;
					if(from_hold == 0 && sqlMatch(sqlChars, index , FROM)){
						from_hold = index;
					}else if(distinct_hold == 0 && sqlMatch(sqlChars, index , DISTINCT)){
						distinct_hold = index;
					}else if(group_hold == 0 && sqlMatch(sqlChars, index , GROUP)){
						group_hold = index;
					}else if(union_hold == 0 && sqlMatch(sqlChars, index , UNION)){
						union_hold = index;
					}else if(order_hold == 0 && sqlMatch(sqlChars, index , ORDER)){
						order_hold = index;
					}
				}
			}
		}

		if (from_hold == 0) {
			count.setExecuteCount(false);
			return count; // 没有找到from return 1
		}

		String cSql = "";
		if(distinct_hold > 0 || union_hold > 0){
			if(order_hold > 0){
				cSql = "select count(1) from (" + querySql.substring(0, order_hold) + ") v";
			}else{
				cSql = "select count(1) from (" + querySql + ") v";
			}
		}else if (order_hold == 0) {
			if (group_hold > 0) {
				cSql = "select count(1) from (select 1 " + querySql.substring(from_hold) + ") v";
			} else {
				cSql = "select count(1) " + querySql.substring(from_hold);
			}
		} else if(order_hold > 0){
			if (group_hold > 0) {
				cSql = "select count(1) from (select 1 " + querySql.substring(from_hold, order_hold) + ") v";
			} else {
				cSql = "select count(1) " + querySql.substring(from_hold, order_hold);
			}
		}

		count.setCountSql(cSql);
		return count;
	}

	/**
	 * 统计sql查询总共的条数
	 *
	 */
	private void countItems(Context context,Count count ,Map<String, Param> params, String provideName) {

		String cSql = count.getCountSql();
		Connection conn = context.getConn(provideName);

		List<Object> exeParam = new ArrayList<>();
		String ceSql = getExecuteSql(cSql,params, exeParam);

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(ceSql);
			if (queryTimeout != null) {
				statement.setQueryTimeout(queryTimeout);
			}
			// 参数设定
			initParam(statement, exeParam);
			dataset = statement.executeQuery();
			dataset.next();
			count.setTotalCounts( dataset.getLong(1));
		} catch (SQLException e) {
			throw new SqlExecuteException(provideName, getSqlTask(context,"QueryCount"), ceSql, e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			LogUtil.info("Execute countSql：" + ceSql);
		}
	}

	private boolean sqlMatch(char[] sqls, int index, char[]match){
		if(index + match.length + 1 >= sqls.length  ){
			return false;
		}
		for (int i =0; i< match.length; i++, index++){
			if(sqls[index] != match[i] && sqls[index]+32 != match[i]){
				return false;
			}
		}
		return sqls[index] == ' ' || sqls[index] == '\n' || sqls[index] == '\t' || sqls[index] == '(';
	}

	private void prePager(Context context){
		Map<String,Object> params = context.getParamDatas();
		Object start = params.get("start");
		if (DataUtil.isNotNull(start)) {
			context.setStartWith(Long.parseLong(start.toString()));
		}
		Object limit = params.get("limit");
		if (DataUtil.isNotNull(limit)) {
			context.setPagerSize(Integer.parseInt(limit.toString()));
		}
		Object count = params.get("count");
		if(DataUtil.isNotNull(count)) {
			CountType countType = (CountType) EnumHandlerFactory.getEnumHandler(CountType.class).locateEnum(count.toString().toLowerCase());
			if(countType != null){
				context.setCountType(countType);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Param> getParams() {
		return params;
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public Integer getQueryTimeout() {
		return queryTimeout;
	}

	public void setQueryTimeout(Integer queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public Integer getPagerSize() {
		return pagerSize;
	}

	public void setPagerSize(Integer pagerSize) {
		this.pagerSize = pagerSize;
	}

	public String getAdapter() {
		return adapter;
	}

	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}

	public QueryAdapter getQueryAdapter() {
		return queryAdapter;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public Integer getExportSize() {
		return exportSize;
	}

	public void setExportSize(Integer exportSize) {
		this.exportSize = exportSize;
	}

	public Integer getMaxPagerSize() {
		return maxPagerSize;
	}

	public void setMaxPagerSize(Integer maxPagerSize) {
		this.maxPagerSize = maxPagerSize;
	}

	public String getConnectionProvide() {
		return connectionProvide;
	}

	public void setConnectionProvide(String connectionProvide) {
		this.connectionProvide = connectionProvide;
	}

	public void setSql(Sql sql) {
		this.sql = sql;
	}

	@Override
	public void execute(Context context, Map<String, Param> params, String provideName) {
		if(DataUtil.isNull(rootPath)){
			throw new DBFoundRuntimeException("rootPath can not be null");
		}
		final String currentPath = context.getCurrentPath();
		final String currentModel = context.getCurrentModel();

		String exePath = this.currentPath;
		if(DataUtil.isNotNull(exePath)){
			if(!ELEngine.isAbsolutePath(exePath)) {
				exePath = currentPath +"." + exePath;
			}
		}else{
			exePath = currentPath;
		}
		String mName = DataUtil.isNull(modelName) ? currentModel : modelName;
		List data = ModelEngine.query(context, mName, name, exePath, false, entityClass).getDatas();

		String setPath = rootPath;
		if(!ELEngine.isAbsolutePath(setPath)){
			setPath = currentPath + "." + setPath;
		}
		context.setData(setPath, data);
		context.setCurrentPath(currentPath);
		context.setCurrentModel(currentModel);
	}

}
