package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.db.dialect.AbstractSqlDialect;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.adapter.AdapterFactory;
import com.nfwork.dbfound.model.adapter.QueryAdapter;
import com.nfwork.dbfound.model.base.Count;
import com.nfwork.dbfound.model.dsql.DSqlEngine;
import com.nfwork.dbfound.util.*;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.reflector.ReflectorUtil;

public class Query extends SqlEntity {

	private static final long serialVersionUID = 83009892861541099L;

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
	private String adapter;
	private QueryAdapter queryAdapter;
	private String entity;
	private Class entityClass;
	private String currentPath;

	@Override
	public void init(Element element) {
		params = new HashMap<>();
		filters = new HashMap<>();
		super.init(element);
	}

	@Override
	public void run() {
		if(DataUtil.isNotNull(adapter)){
			try {
				queryAdapter = AdapterFactory.getQueryAdapter( Class.forName(adapter));
			}catch (Exception exception){
				String message = "queryAdapter init failed, queryAdapter must implement QueryAdapter";
				throw new DBFoundPackageException(message,exception);
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

		if(queryAdapter !=null) {
			Class qClass = queryAdapter.getEntityClass();
			if (qClass != null) {
				entityClass = qClass;
			}
		}

		if (getParent() instanceof Model) {
			Model model = (Model) getParent();
			if (name == null || "".equals(name)) {
				model.putQuery("_default", this);
			} else {
				model.putQuery(name, this);
			}
			if(DataUtil.isNotNull(sql)) {
				autoCreateParam(sql, params);
			}
		} else {
			super.run();
		}
	}

	public Map<String, Param> cloneParams() {
		HashMap<String, Param> params = new HashMap<>();
		for(Map.Entry<String,Param> entry : this.params.entrySet()){
			params.put(entry.getKey(), (Param) entry.getValue().cloneEntity());
		}
		return params;
	}

	public HashMap<String, Filter> cloneFilters() {
		HashMap<String, Filter> filters = new HashMap<>();
		for(Map.Entry<String,Filter> entry : this.filters.entrySet()){
			filters.put(entry.getKey(), (Filter) entry.getValue().cloneEntity());
		}
		return filters;
	}

	public String getQuerySql(Context context,Map<String, Param> params, String provideName){
		String querySql = initFilter(sql, params, context, provideName);
		querySql = staticParamParse(querySql, params, context);
		return querySql.trim();
	}

	public <T> List<T> query(Context context, String querySql, Map<String, Param> params, String provideName, Class<T> clazz, boolean autoPaging) {
		Connection conn = context.getConn(provideName);

		List<Map> data = new ArrayList<>();
		List<Object> exeParam = new ArrayList<>();

		if(autoPaging) {
			if (context.getPagerSize() > 0 || pagerSize != null) {
				int ps = context.getPagerSize() > 0 ? context.getPagerSize() : pagerSize;
				SqlDialect dialect = context.getConnDialect(provideName);
				if(dialect instanceof AbstractSqlDialect){
					AbstractSqlDialect sqlDialect = (AbstractSqlDialect) dialect;
					querySql = sqlDialect.getPagerSql(querySql, ps, context.getStartWith(),params);
				}else{
					querySql = dialect.getPagerSql(querySql, ps, context.getStartWith());
				}
			}
		}
		String eSql = getExecuteSql(querySql,params, exeParam, context);

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
				return ReflectorUtil.parseResultList(clazz, dataset, context);
			}

			String[] colNames = getColNames(metaset);

			int totalCounts = 0;
			Calendar defaultCalendar = Calendar.getInstance();
			while (dataset.next()) {
				if (context.isQueryLimit() && ++totalCounts > context.getQueryLimitSize()) {
					break;
				}
				Map<String, Object> mapdata = new HashMap<>();
				for (int i = 1; i <= colNames.length; i++) {
					String value = dataset.getString(i);
					String columnName = colNames[i-1];
					if ("d_p_rm".equals(columnName)) {// 分页参数 不放入map
						continue;
					}
					if (value == null) {
						mapdata.put(columnName, null);
						continue;
					}
					int columnType = metaset.getColumnType(i);
					mapdata.put(columnName,getData(value,columnType,dataset,i,defaultCalendar));
				}
				data.add(mapdata);
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("Query执行异常:" + e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			LogUtil.log("querySql",eSql, params.values(),context);
		}
		return (List<T>) data;
	}

	private final static Pattern p = Pattern.compile("#[A-Z_]+#");

	private String initFilter(String ssql,Map<String, Param> params, Context context, String provideName) {
		StringBuilder bfsql = new StringBuilder();
		for (Param param : params.values()) {
			if (param instanceof Filter){
				Filter nfFilter = (Filter) param;

				//condition 逻辑判断；
				if (DataUtil.isNotNull(nfFilter.getCondition())) {
					String conditionSql = staticParamParse(nfFilter.getCondition(), params, context);
					List<Object> exeParam = new ArrayList<>();
					conditionSql = getExecuteSql(conditionSql, params, exeParam, context);
					Boolean result = DSqlEngine.checkWhenSql(conditionSql, exeParam, provideName, context);
					if (result == null) {
						throw new DBFoundRuntimeException("Filter condition express is not support, condition:" + nfFilter.getCondition());
					}
					if (!result) {
						continue;
					}
				}
				bfsql.append(nfFilter.getExpress()).append(" and ");
			}
		}
		String fsql = bfsql.length() > 4 ? bfsql.substring(0, bfsql.length() - 4) : null;
		if (fsql != null) {
			fsql = Matcher.quoteReplacement(fsql);
		}

		Matcher m = p.matcher(ssql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String text = m.group();
			if (text.equals(WHERE_CLAUSE)) {
				if (fsql == null) {
					m.appendReplacement(buf, " ");
				} else {
					m.appendReplacement(buf, " where " + fsql);
				}
			} else if (text.equals(AND_CLAUSE)) {
				if (fsql == null) {
					m.appendReplacement(buf, " ");
				} else {
					m.appendReplacement(buf, " and " + fsql);
				}
			}
		}
		m.appendTail(buf);
		return buf.toString();
	}

	public Count getCount(String querySql){

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
			if (sqlChars[i] == '(' && dyh % 2 == 0  && syh % 2 ==0) {
				kh++;
			} else if (sqlChars[i] == ')' && dyh % 2 == 0 && syh % 2 ==0) {
				kh--;
			} else if (sqlChars[i] == '\'' && sqlChars[i-1] != '\\' && syh % 2==0) {
				dyh++;
			} else if (sqlChars[i] == '\"' && sqlChars[i-1] != '\\' && dyh % 2==0) {
				syh++;
			}
			if (sqlChars[i] == ' ' || sqlChars[i] == '\n' || sqlChars[i] == '\t' || sqlChars[i] == ')' ) {
				if (kh == 0 && dyh % 2 == 0 && syh % 2 ==0) {
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
				cSql = "select count(1) from (select 1 " + querySql.substring(from_hold) + " ) v";
			} else {
				cSql = "select count(1) " + querySql.substring(from_hold);
			}
		} else if(order_hold > 0){
			if (group_hold > 0) {
				cSql = "select count(1) from (select 1 " + querySql.substring(from_hold, order_hold) + " ) v";
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
	public void countItems(Context context,Count count ,Map<String, Param> params, String provideName) {

		String cSql = count.getCountSql();
		Connection conn = context.getConn(provideName);

		List<Object> exeParam = new ArrayList<>();
		String ceSql = getExecuteSql(cSql,params, exeParam, context);

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
			throw new DBFoundPackageException("Query execute count exception:" + e.getMessage(), e);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Param> getParams() {
		return params;
	}

	public void setParams(Map<String, Param> params) {
		this.params = params;
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Filter> filters) {
		this.filters = filters;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
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
		String mName = modelName != null?modelName : currentModel;
		List data = InnerModelExe.innerQuery(context, mName, name, exePath, false, entityClass).getDatas();

		String setPath = rootPath;
		if(!ELEngine.isAbsolutePath(setPath)){
			setPath = currentPath + "." + setPath;
		}
		context.setData(setPath, data);
		context.setCurrentPath(currentPath);
		context.setCurrentModel(currentModel);
	}

}
