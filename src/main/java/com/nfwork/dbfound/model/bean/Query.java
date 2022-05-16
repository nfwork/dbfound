package com.nfwork.dbfound.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.util.*;
import org.dom4j.Element;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.model.reflector.ReflectorUtil;

public class Query extends SqlEntity {

	private static final long serialVersionUID = 83009892861541099L;
	private String name = "_default"; // query对象的名字
	private int pagerSize;
	private long startWith;
	private long length; // 总共条数
	private Map<String, Param> params; // query对象对应参数
	private Map<String, Filter> filters;
	private String currentPath;
	private String rootPath;
	private String modelName;
	private Integer queryTimeout;
	private static final String WHERE_CLAUSE = "#WHERE_CLAUSE#";
	private static final String AND_CLAUSE = "#AND_CLAUSE#";

	@Override
	public void init(Element element) {
		params = new HashMap<String, Param>();
		filters = new HashMap<String, Filter>();
		super.init(element);
	}

	@Override
	public void run() {
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

	public Query cloneEntity() {
		Query query;
		try {
			query = (Query) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new DBFoundPackageException("query克隆异常:" + e.getMessage(), e);
		}
		HashMap<String, Param> params = new HashMap<String, Param>();
		HashMap<String, Filter> filters = new HashMap<String, Filter>();

		for (Iterator iterator = this.params.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Param param = (Param) entry.getValue();
			params.put(entry.getKey().toString(), (Param) param.cloneEntity());
		}
		for (Iterator iterator = this.filters.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Filter filter = (Filter) entry.getValue();
			if (entry.getKey() != null) {
				filters.put(entry.getKey().toString(), (Filter) filter.cloneEntity());
			}
		}

		query.setParams(params);
		query.setFilters(filters);
		return query;
	}

	/**
	 * 查询结构集 以list的map对象返回
	 * 
	 * @param context
	 * @param provideName
	 * @param object
	 * @return
	 */
	public <T> List<T> query(Context context, String provideName, Class<T> object) {
		Connection conn = context.getConn(provideName);
		SqlDialect dialect = context.getConnDialect(provideName);

		sql = initFilter(sql);
		sql = staticParamParse(sql, params);

		// fileter初始化，数据库方言初始化
		sql = dialect.parseSql(sql);

		List<Map> data = new ArrayList<Map>();
		String eSql = getExecuteSql(sql,params);

		if (pagerSize > 0) {
			eSql = dialect.getPagerSql(eSql, pagerSize, startWith);
		}

		PreparedStatement statement = null;
		ResultSet dataset = null;
		try {
			statement = conn.prepareStatement(eSql);
			if (queryTimeout != null) {
				statement.setQueryTimeout(queryTimeout);
			}
			// 参数设定
			initParam(statement, this.sql, params);
			dataset = statement.executeQuery();
			ResultSetMetaData metaset = dataset.getMetaData();

			// 如果对象不为null，利用反射构建object对象
			if (object != null) {
				List<T> list = (List<T>) ReflectorUtil.parseResultList(object, dataset, context);
				return list;
			}

			int size = metaset.getColumnCount();
			String colNames[] = new String[size + 1];
			for (int i = 1; i < colNames.length; i++) {
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
				colNames[i] = colName;
			}
			int totalCounts = 0;
			Calendar defaultCalendar = Calendar.getInstance();
			while (dataset.next()) {
				if (context.queryLimit && ++totalCounts > context.queryLimitSize) {
					break;
				}
				Map<String, Object> mapdata = new HashMap<String, Object>();
				for (int i = 1; i <= size; i++) {
					String value = dataset.getString(i);
					String columnName = colNames[i];
					if ("d_p_rm".equals(columnName)) {// 分页参数 不放入map
						continue;
					}
					int columnType = metaset.getColumnType(i);
					if (value == null) {
						mapdata.put(columnName, null);
						continue;
					}
					switch (columnType) {
						case Types.INTEGER:
						case Types.TINYINT:
						case Types.SMALLINT:
							mapdata.put(columnName, dataset.getInt(i));
							break;
						case Types.BIGINT:
							mapdata.put(columnName, dataset.getLong(i));
							break;
						case Types.FLOAT:
						case Types.REAL:
							if (value.endsWith(".0") || !value.contains(".")) {
								mapdata.put(columnName, dataset.getInt(i));
							} else {
								mapdata.put(columnName, dataset.getFloat(i));
							}
							break;
						case Types.DOUBLE:
						case Types.DECIMAL:
						case Types.NUMERIC:
							if (value.endsWith(".0") || !value.contains(".")) {
								mapdata.put(columnName, dataset.getLong(i));
							} else {
								mapdata.put(columnName, dataset.getDouble(i));
							}
							break;
						case Types.VARBINARY:
							if (value.matches("[0123456789]*\\.[0123456789]+")) {
								mapdata.put(columnName, dataset.getDouble(i));
							} else if (value.matches("[0123456789]*")) {
								mapdata.put(columnName, dataset.getLong(i));
							} else {
								mapdata.put(columnName, value);
							}
							break;
						case Types.DATE:
							mapdata.put(columnName, dataset.getDate(i, defaultCalendar));
							break;
						case Types.TIME:
						case Types.TIMESTAMP:
							mapdata.put(columnName, dataset.getTimestamp(i, defaultCalendar));
							break;
						case Types.BOOLEAN:
							mapdata.put(columnName, dataset.getBoolean(i));
							break;
						default:
							mapdata.put(columnName, value);
					}
				}
				data.add(mapdata);
			}
		} catch (SQLException e) {
			throw new DBFoundPackageException("Query执行异常:" + e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			LogUtil.log(eSql, params);
		}
		return (List<T>) data;
	}

	/**
	 * 初始化过滤条件
	 * 
	 * @param ssql
	 * @return
	 */
	public String initFilter(String ssql) {

		StringBuffer bfsql = new StringBuffer();
		Collection<Filter> params = filters.values();
		for (Filter nfFilter : params) {
			if (nfFilter.isActive()) {
				bfsql.append(nfFilter.getExpress()).append(" and ");
			}
		}
		String fsql = bfsql.length() > 4 ? bfsql.substring(0, bfsql.length() - 4) : null;
		if (fsql != null) {
			fsql = Matcher.quoteReplacement(fsql);
		}

		Pattern p = Pattern.compile("\\#[A-Z_]+\\#");
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

	/**
	 * 统计sql查询总共的条数
	 * 
	 * @return
	 */
	public long countItems(Connection conn) {
		char[] sqlChars = sql.toLowerCase().toCharArray();
		int dyh = 0;
		int kh = 0;
		int from_hold = 0;
		int order_hold = 0;
		int group_hold = 0;

		// 寻找from的位置
		for (int i = 6; i < sqlChars.length - 4; i++) {
			if (sqlChars[i] == '(') {
				kh++;
			} else if (sqlChars[i] == ')') {
				kh--;
			} else if (sqlChars[i] == '\'') {
				dyh++;
			} else if (sqlChars[i] == 'f') {
				if (sqlChars[i + 1] == 'r') {
					if (sqlChars[i + 2] == 'o') {
						if (sqlChars[i + 3] == 'm') {
							if (sqlChars[i + 4] == ' ' || sqlChars[i + 4] == '\n' || sqlChars[i + 4] == '\t') {
								if (kh == 0 && (dyh % 2 == 0)
										&& (sqlChars[i - 1] == ' ' || sqlChars[i - 1] == ')' || sqlChars[i - 1] == '\n' || sqlChars[i - 1] == '\t')) {
									from_hold = i;
									break;
								} else {
									i = i + 4;
								}
							} else {
								i = i + 3;
							}
						} else {
							i = i + 2;
						}
					} else {
						i = i + 1;
					}
				}
			}
		}

		if (from_hold == 0) {
			return 1; // 没有找到from return 1
		}

		// 寻找order by的位置,group by 位置
		for (int i = from_hold + 4; i < sqlChars.length - 8; i++) {
			if (sqlChars[i] == '(') {
				kh++;
			} else if (sqlChars[i] == ')') {
				kh--;
			} else if (sqlChars[i] == '\'') {
				dyh++;
			} else if (sqlChars[i] == 'o') {
				if (sqlChars[i + 1] == 'r') {
					if (sqlChars[i + 2] == 'd') {
						if (sqlChars[i + 3] == 'e') {
							if (sqlChars[i + 4] == 'r') {
								if (sqlChars[i + 5] == ' ' || sqlChars[i + 5] == '\n' || sqlChars[i + 5] == '\t') {
									if (kh == 0
											&& (dyh % 2 == 0)
											&& (sqlChars[i - 1] == ' ' || sqlChars[i - 1] == ')' || sqlChars[i - 1] == '\n' || sqlChars[i - 1] == '\t')) {
										order_hold = i;
										break;
									} else {
										i = i + 5;
									}
								} else {
									i = i + 4;
								}
							} else {
								i = i + 3;
							}
						} else {
							i = i + 2;
						}
					} else {
						i = i + 1;
					}
				}
			} else if (sqlChars[i] == 'g') {
				if (sqlChars[i + 1] == 'r') {
					if (sqlChars[i + 2] == 'o') {
						if (sqlChars[i + 3] == 'u') {
							if (sqlChars[i + 4] == 'p') {
								if (sqlChars[i + 5] == ' ' || sqlChars[i + 5] == '\n' || sqlChars[i + 5] == '\t') {
									if (kh == 0
											&& (dyh % 2 == 0)
											&& (sqlChars[i - 1] == ' ' || sqlChars[i - 1] == ')' || sqlChars[i - 1] == '\n' || sqlChars[i - 1] == '\t')) {
										group_hold = i;
										break;
									} else {
										i = i + 5;
									}
								} else {
									i = i + 4;
								}
							} else {
								i = i + 3;
							}
						} else {
							i = i + 2;
						}
					} else {
						i = i + 1;
					}
				}
			}
		}

		String cSql = "";
		if (order_hold == 0) {
			if (group_hold > 0) {
				cSql = "select count(1) from (select 1 " + sql.substring(from_hold) + " ) v";
			} else {
				cSql = "select count(1) " + sql.substring(from_hold);
			}
		} else {
			cSql = "select count(1) " + sql.substring(from_hold, order_hold);
		}
		String ceSql = getExecuteSql(cSql,params);
		PreparedStatement statement = null;
		ResultSet dataset = null;
		long count = 0;
		try {
			statement = conn.prepareStatement(ceSql);
			// 参数设定
			initParam(statement, cSql, params);
			dataset = statement.executeQuery();
			dataset.next();
			count = dataset.getLong(1);
			length = count;
		} catch (SQLException e) {
			throw new DBFoundPackageException("Query执行count查询异常:" + e.getMessage(), e);
		} finally {
			DBUtil.closeResultSet(dataset);
			DBUtil.closeStatement(statement);
			LogUtil.info("execute count sql：" + ceSql);
		}
		return count;
	}

	/**
	 * 设定参数
	 * 
	 * @param name
	 * @param value
	 */
	public void setParam(String name, String value) {
		params.get(name).setValue(value);
	}

	/**
	 * 得到参数值
	 * 
	 * @param name
	 * @return
	 */
	public String getParam(String name) {
		return params.get(name).getStringValue();
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

	public int getPagerSize() {
		return pagerSize;
	}

	public void setPagerSize(int pagerSize) {
		this.pagerSize = pagerSize;
	}

	public long getStartWith() {
		return startWith;
	}

	public void setStartWith(long startWith) {
		this.startWith = startWith;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long dataLength) {
		this.length = dataLength;
	}

	public Map<String, Filter> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Filter> filters) {
		this.filters = filters;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
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

	@Override
	public void execute(Context context, Map<String, Param> params, String provideName) {
		String currentPath = context.getCurrentPath();
		if (modelName == null) {
			modelName = context.getCurrentModel();
		}
		List<Map> datas = ModelEngine.query(context, modelName, name, currentPath, false).getDatas();
		context.setData(rootPath, datas);
	}

}
