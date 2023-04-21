package com.nfwork.dbfound.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelCache;
import com.nfwork.dbfound.model.bean.Model;
import com.nfwork.dbfound.util.*;
import com.nfwork.dbfound.web.WebWriter;

public class Context {

	private boolean outMessage = true;
	private boolean isExport = false;
	private boolean queryLimit = DBFoundConfig.getQueryLimit();
	private int queryLimitSize = DBFoundConfig.getQueryLimitSize();
	private int reportQueryLimitSize = DBFoundConfig.getReportQueryLimitSize();
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat dateTimeFormat;
	public HttpServletRequest request;
	public HttpServletResponse response;

	private int pagerSize = 0;
	private long startWith = 0;

	private String currentPath;
	private String currentModel;
	private Map<String, ConnObject> connMap;
	private boolean inWebContainer;
	private final Map<String, Object> rootDatas;
	private Map<String, Object> paramDatas;
	private Map<String, Object> outParamDatas;
	private Map<String, Object> requestDatas;
	private Map<String, Object> sessionDatas;
	private Map<String, Object> cookieDatas;
	private Map<String, Object> headerDatas;

	private Transaction transaction;
	private final long createThread = Thread.currentThread().getId();
	private Map<String,String> underLineNameCache ;

	public Transaction getTransaction() {
		checkContext();
		if(transaction == null){
			transaction = new Transaction();
		}
		return transaction;
	}

	/**
	 * 得到当前 context，是否需要开启session
	 * 
	 * @param request request
	 * @param response response
	 * @return Context
	 */
	public static Context getCurrentContext(HttpServletRequest request, HttpServletResponse response) {
		Object context = request.getAttribute("_currentContext");
		if (context == null) {
			context = new Context(request, response);
			request.setAttribute("_currentContext", context);
		}
		return (Context) context;
	}

	public Context() {
		rootDatas = new HashMap<>();
	}

	/**
	 * 根据map生成一个thread对象
	 * 
	 * @param datas  map data
	 */
	public Context(Map<String, Object> datas) {
		if (datas == null) {
			datas = new HashMap<>();
		}
		rootDatas = datas;
	}

	private Context(HttpServletRequest request, HttpServletResponse response) {
		rootDatas = new HashMap<>();

		cloneParamData(request);
		cloneRequestData(request);
		cloneRequestBodyData(request);
		cloneHeaderData(request);
		cloneCookieData(request);
		if (DBFoundConfig.isOpenSession()) {
			cloneSessionData(request.getSession());
		}

		inWebContainer = true;
		this.request = request;
		this.response = response;
	}

	/**
	 * cloneCookieData
	 * 
	 * @param request http request
	 */
	public void cloneCookieData(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName() != null) {
					getCookieDatas().put(cookie.getName(), cookie);
				}
			}
		}
	}

	/**
	 * clone header data
	 * 
	 * @param request http request
	 */
	public void cloneHeaderData(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key != null) {
				getHeaderDatas().put(key, request.getHeader(key));
			}
		}
	}

	/**
	 * 复制session数据
	 */
	public void cloneSessionData(HttpSession session) {
		if (!DBFoundConfig.isOpenSession()) {
			throw new DBFoundRuntimeException("session is not opened, can not set data to session ");
		}
		Enumeration<String> enumeration = session.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			if (paramName.contains(".")) {
				continue; // 初始化复制session数据时，不克隆a.b多层次数据。
			}
			getSessionDatas().put(paramName, session.getAttribute(paramName));
		}
	}

	/**
	 * 复制param数据
	 */
	public void cloneParamData(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			String value = request.getParameter(paramName);
			Object object =value;
			if(DBFoundConfig.isJsonStringAutoCover() || DBFoundConfig.getJsonStringForceCoverSet().contains(paramName)){
				if (value.startsWith("{") && value.endsWith("}")) {
					object = JsonUtil.jsonToMap(value);
				} else if (value.startsWith("[") && value.endsWith("]")) {
					object = JsonUtil.jsonToList(value);
				}
			}
			setParamData(paramName, object);
		}
	}

	/**
	 * 复制request数据
	 */
	public void cloneRequestData(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			if ("_currentContext".equals(paramName)) {
				continue;
			} else if (paramName.contains(".")) {
				continue; // 初始化复制request数据时，不克隆a.b多层次数据。
			}
			getRequestDatas().put(paramName, request.getAttribute(paramName));
		}
	}

	/**
	 * 复制requestBody数据
	 */
	public void cloneRequestBodyData(HttpServletRequest request) {
		try {
			String contentType = request.getHeader("Content-Type");
			if (contentType != null && contentType.contains("application/json")) {
				try (InputStream inputStream = request.getInputStream()){
					String payload = StreamUtils.copyToString(inputStream, Charset.forName(DBFoundConfig.getEncoding())).trim();
					if (payload.startsWith("{")) {
						Map<String, Object> map = JsonUtil.jsonToMap(payload);
						for (Map.Entry<String, Object> entry : map.entrySet()) {
							setParamData(entry.getKey(), entry.getValue());
						}
					} else if (payload.startsWith("[")) {
						List list = JsonUtil.jsonToList(payload);
						setParamData("dataList", list);
					}
				}
			}
		}catch (IOException exception){
			throw new DBFoundRuntimeException(exception);
		}
	}

	public Object getData(String express) {
		return DBFoundEL.getData(express, rootDatas);
	}

	public Object getData(String express, Map<String, Object> elCache) {
		return DBFoundEL.getData(express, rootDatas, elCache);
	}

	public int getDataLength(String express){
		int dataSize = -1;
		Object data = this.getData(express);
		if(data != null) {
			dataSize = DataUtil.getDataLength(data);
		}
		return dataSize;
	}

	/**
	 * 根据表达式得到context内容
	 * 
	 * @param express express
	 * @param class1 class
	 * @return T
	 */
	public <T> T getData(String express, Class<T> class1) {
		Object object = getData(express);
		if (object == null) {
			return null;
		} else if (object.getClass().equals(class1)) {
		} else if (class1.equals(Integer.class) || class1.equals(int.class)) {
			object = DataUtil.intValue(object);
		} else if (class1.equals(Long.class) || class1.equals(long.class)) {
			object = DataUtil.longValue(object);
		} else if (class1.equals(Float.class) || class1.equals(float.class)) {
			object = DataUtil.floatValue(object);
		} else if (class1.equals(Double.class) || class1.equals(double.class)) {
			object = DataUtil.doubleValue(object);
		} else if (class1.equals(Date.class)) {
			object = DataUtil.dateValue(object);
		} else if (class1.equals(String.class)) {
			object = DataUtil.stringValue(object);
		} else if (class1.equals(Short.class) || class1.equals(short.class)) {
			object = DataUtil.shortValue(object);
		} else if (class1.equals(Byte.class) || class1.equals(byte.class)) {
			object = DataUtil.byteValue(object);
		}
		return (T) object;
	}

	public Map<String, Object> getDatas() {
		return rootDatas;
	}

	public String getString(String express) {
		Object object = getData(express);
		if (object != null) {
			return object.toString();
		}
		return null;
	}

	public void setData(String name, Object object) {
		if(DataUtil.isNull(name)){
			throw new DBFoundRuntimeException("name can not be null");
		}
		if (name.startsWith(ELEngine.paramScope)) {
			name = name.substring(6);
			setParamData(name, object);
		} else if (name.startsWith(ELEngine.outParamScope)) {
			name = name.substring(9);
			setOutParamData(name, object);
		} else if (name.startsWith(ELEngine.requestScope)) {
			name = name.substring(8);
			setRequestData(name, object);
		} else if (name.startsWith(ELEngine.sessionScope)) {
			name = name.substring(8);
			setSessionData(name, object);
		} else  {
			throw new DBFoundRuntimeException("context only in (param,request,session,outParam) can set data");
		}
	}

	/**
	 * 放参数到param集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setParamData(String name, Object value) {
		if (name.contains(".")) {
			DBFoundEL.setData(name,getParamDatas(),value);
		}else{
			getParamDatas().put(name, value);
		}
	}

	/**
	 * 放参数到outParam集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setOutParamData(String name, Object value) {
		if (name.contains(".")) {
			DBFoundEL.setData(name,getOutParamDatas(),value);
		}else{
			getOutParamDatas().put(name, value);
		}
	}

	/**
	 * 放参数到request集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setRequestData(String name, Object value) {
		if (name.contains(".")) {
			throw new DBFoundRuntimeException("param name can not be contain '.' :" + name);
		}
		if (request != null) {
			request.setAttribute(name, value);
		}
		getRequestDatas().put(name, value);
	}

	/**
	 * 放参数到session集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setSessionData(String name, Object value) {
		if (!DBFoundConfig.isOpenSession()) {
			throw new DBFoundRuntimeException("session is not opened, can not set data to session ");
		}
		if (name.contains(".")) {
			throw new DBFoundRuntimeException("param name can not be contain '.' :" + name);
		}
		if (request != null) {
			request.getSession().setAttribute(name, value);
		}
		getSessionDatas().put(name, value);
	}

	/**
	 * 获取model
	 * 
	 * @param modelName model name
	 * @return model
	 */
	public Model getModel(String modelName) {
		Model model = ModelCache.get(modelName);

		if(!model.isPkgModel() && DBFoundConfig.isModelModifyCheck()) {
			File file = new File(model.getFileLocation());
			long newFileLastModify = file.lastModified();
			if (newFileLastModify > model.getFileLastModify()) {
				ModelCache.remove(modelName);
				model = ModelCache.get(modelName);
			}
		}

		return model;
	}

	/**
	 * 得到数据库连接
	 * 
	 * @param provideName provide name
	 * @return Connection
	 */
	public Connection getConn(String provideName) {

		checkContext();

		if (transaction !=null && transaction.isOpen()) {
			if (transaction.connMap == null) {
				transaction.connMap = new HashMap<String, ConnObject>();
			}
			ConnObject connObject = transaction.connMap.get(provideName);
			if (connObject == null) {
				ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
				Connection conn = provide.getConnection();
				if(transaction.getTransactionIsolation()>0) {
					try {
						int currentIsolation = conn.getTransactionIsolation();
						if(currentIsolation != transaction.getTransactionIsolation()) {
							conn.setTransactionIsolation(transaction.getTransactionIsolation());
						}
					} catch (SQLException e) {
						throw new DBFoundPackageException("set transaction isolation failed", e);
					}
				}
				provide.closeAutoCommit(conn);
				connObject = new ConnObject(provide, conn);
				transaction.connMap.put(provideName, connObject);
			}
			return connObject.connection;
		} else {
			if (connMap == null) {
				connMap = new HashMap<String, ConnObject>();
			}
			ConnObject connObject = connMap.get(provideName);
			if (connObject == null) {
				ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
				Connection conn = provide.getConnection();
				connObject = new ConnObject(provide, conn);
				connMap.put(provideName, connObject);
			}
			return connObject.connection;
		}
	}

	public String getUnderLineNameByCache(String property){
		if(underLineNameCache == null){
			underLineNameCache = new HashMap<>();
		}
		String value = underLineNameCache.get(property);
		if(value == null){
			value = StringUtil.camelCaseToUnderscore(property);
			underLineNameCache.put(property,value);
		}
		return value;
	}

	/**
	 * 得到默认数据库连接
	 * 
	 * @return Connection
	 * @throws SQLException sql exception
	 */
	public Connection getConn() throws SQLException {
		return getConn("_default");
	}

	public SqlDialect getConnDialect(String provideName) {
		checkContext();
		ConnectionProvide provide;
		if (transaction !=null && transaction.isOpen()) {
			provide = transaction.connMap.get(provideName).provide;
		} else {
			provide = connMap.get(provideName).provide;
		}
		return provide.getSqlDialect();
	}

	/**
	 * 关闭连接
	 */
	public void closeConns() {
		checkContext();
		if (connMap == null || connMap.isEmpty()) {
			return;
		}
		Collection<ConnObject> connObjects = connMap.values();
		for (ConnObject connObject : connObjects) {
			try {
				ConnectionProvide provide = connObject.provide;
				Connection connection = connObject.connection;
				provide.closeConnection(connection);
			} catch (Exception e) {
				LogUtil.error("database connection close exception:" + e.getMessage(), e);
			}
		}
		connMap.clear();
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public String getCurrentModel() {
		return currentModel;
	}

	public void setCurrentModel(String currentModel) {
		this.currentModel = currentModel;
	}

	public Map<String, Object> getOutParamDatas() {
		if (outParamDatas == null) {
			Object o = rootDatas.get("outParam");
			if (o instanceof Map) {
				outParamDatas = (Map) o;
			}else{
				outParamDatas = new HashMap<>();
				rootDatas.put("outParam",outParamDatas);
			}
		}
		return outParamDatas;
	}

	public Map<String, Object> getParamDatas() {
		if (paramDatas == null) {
			Object o = rootDatas.get("param");
			if (o instanceof Map) {
				paramDatas = (Map) o;
			} else {
				paramDatas = new HashMap<String, Object>();
				rootDatas.put("param", paramDatas);
			}
		}
		return paramDatas;
	}

	public Map<String, Object> getRequestDatas() {
		if (requestDatas == null) {
			Object o = rootDatas.get("request");
			if (o instanceof Map) {
				requestDatas = (Map) o;
			} else {
				requestDatas = new HashMap<String, Object>();
				rootDatas.put("request", requestDatas);
			}
		}
		return requestDatas;
	}

	public Map<String, Object> getSessionDatas() {
		if (!DBFoundConfig.isOpenSession()) {
			throw new DBFoundRuntimeException("session is not opened, can not get data from session ");
		}
		if (sessionDatas == null) {
			Object o = rootDatas.get("session");
			if (o instanceof Map) {
				sessionDatas = (Map) o;
			} else {
				sessionDatas = new HashMap<String, Object>();
				rootDatas.put("session", sessionDatas);
			}
		}
		return sessionDatas;
	}

	public Map<String, Object> getCookieDatas() {
		if (cookieDatas == null) {
			Object o = rootDatas.get("cookie");
			if (o instanceof Map) {
				cookieDatas = (Map) o;
			} else {
				cookieDatas = new HashMap<String, Object>();
				rootDatas.put("cookie", cookieDatas);
			}
		}
		return cookieDatas;
	}

	public Map<String, Object> getHeaderDatas() {
		if (headerDatas == null) {
			Object o = rootDatas.get("header");
			if (o instanceof Map) {
				headerDatas = (Map) o;
			} else {
				headerDatas = new HashMap<String, Object>();
				rootDatas.put("header", headerDatas);
			}
		}
		return headerDatas;
	}

	public boolean isInWebContainer() {
		return inWebContainer;
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

	public boolean isOutMessage() {
		return outMessage;
	}

	public void setOutMessage(boolean outMessage) {
		this.outMessage = outMessage;
	}

	public boolean isExport() {
		return isExport;
	}

	public void setExport(boolean export) {
		isExport = export;
	}

	public boolean isQueryLimit() {
		return queryLimit;
	}

	public void setQueryLimit(boolean queryLimit) {
		this.queryLimit = queryLimit;
	}

	public int getQueryLimitSize() {
		return queryLimitSize;
	}

	public void setQueryLimitSize(int queryLimitSize) {
		this.queryLimitSize = queryLimitSize;
	}

	public int getReportQueryLimitSize() {
		return reportQueryLimitSize;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void setReportQueryLimitSize(int reportQueryLimitSize) {
		this.reportQueryLimitSize = reportQueryLimitSize;
	}

	public SimpleDateFormat getDateFormat() {
		checkContext();
		if(dateFormat == null){
			dateFormat = new SimpleDateFormat(DBFoundConfig.getDateFormat());
		}
		return dateFormat;
	}

	public SimpleDateFormat getDateTimeFormat() {
		checkContext();
		if(dateTimeFormat == null){
			dateTimeFormat = new SimpleDateFormat(DBFoundConfig.getDateTimeFormat());
		}
		return dateTimeFormat;
	}

	private void checkContext(){
		long runThread = Thread.currentThread().getId();
		if (runThread != createThread) {
			throw new DBFoundRuntimeException("Context can not be user by different thread，create thread id:"+
					createThread + ", run thread id:" + runThread);
		}
	}


	static {
		DBFoundConfig.init();
	}

	static class ConnObject {
		Connection connection;
		ConnectionProvide provide;

		ConnObject(ConnectionProvide provide, Connection connection) {
			this.connection = connection;
			this.provide = provide;
		}
	}
}
