package com.nfwork.dbfound.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.sql.Connection;
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
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.CountType;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.*;

public class Context {

	private boolean outMessage = true;
	private boolean isExport = false;
	public final HttpServletRequest request;
	public final HttpServletResponse response;

	private int pageLimit = 0;
	private long pageStart = 0;
	private CountType countType = CountType.REQUIRED;

	private String currentPath;
	private String currentModel;
	private Map<String, ConnObject> connMap;
	private final Map<String, Object> rootDatas;
	private Map<String, Object> paramDatas;
	private Map<String, Object> outParamDatas;
	private Map<String, Object> requestDatas;
	private Map<String, Object> sessionDatas;
	private Map<String, Object> cookieDatas;
	private Map<String, Object> headerDatas;

	private Transaction transaction;
	private final long createThread = Thread.currentThread().getId();
	private int modelDeep = 0;

	public Transaction getTransaction() {
		checkContext();
		if(transaction == null){
			transaction = new Transaction();
		}
		return transaction;
	}

	/**
	 * 得到当前 context, 是否需要开启session
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
		response = null;
		request = null;
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
		response = null;
		request = null;
	}

	private Context(HttpServletRequest request, HttpServletResponse response) {
		rootDatas = new HashMap<>();

		cloneParamData(request);
		cloneRequestData(request);
		cloneRequestBodyData(request);
		cloneHeaderData(request);
		cloneCookieData(request);
		if (DBFoundConfig.isOpenSession()) {
			cloneSessionData(request.getSession(false));
		}
		this.request = request;
		this.response = response;
	}

	/**
	 * set param for context
	 * @param paramName param name
	 * @param paramValue param value
	 * @return Context
	 */
	public Context withParam(String paramName, Object paramValue) {
		getParamDatas().put(paramName, paramValue);
		return this;
	}

	/**
	 * Expand objects and assign attributes to context
	 * @param bean java bean
	 * @return Context
	 */
	public Context withBeanParam(Object bean) {
		Reflector reflector = Reflector.forClass(bean.getClass());
		reflector.getGetMethods().forEach((propertyName, invoker) -> {
			try {
				Object value  = invoker.invoke(bean,null);
				getParamDatas().put(propertyName, value);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new DBFoundRuntimeException(e);
			}
		});
		return this;
	}

	/**
	 * put all item to the context
	 * @param map params
	 * @return Context
	 */
	public Context withMapParam(Map<String,?> map) {
		getParamDatas().putAll(map);
		return this;
	}

	/**
	 * set query page start with
	 * @param start start with
	 * @return Context
	 */
	public Context withPageStart(long start) {
		this.pageStart = start;
		return this;
	}

	/**
	 * set query pager size
	 * @param limit pager size
	 * @return Context
	 */
	public Context withPageLimit(int limit){
		this.pageLimit = limit;
		return this;
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
		if (session == null) {
			return;
		}
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
			if(DataUtil.isNotNull(value) && ( DBFoundConfig.isJsonStringAutoCover() || DBFoundConfig.getJsonStringForceCoverSet().contains(paramName))){
				if (value.charAt(0)=='{' && value.charAt(value.length()-1)=='}') {
					object = JsonUtil.jsonToMap(value);
				} else if (value.charAt(0)=='[' && value.charAt(value.length()-1)==']') {
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
					if(DataUtil.isNull(payload)){
						return;
					}
					if (payload.charAt(0)=='{') {
						Map<String, Object> map = JsonUtil.jsonToMap(payload);
						for (Map.Entry<String, Object> entry : map.entrySet()) {
							setParamData(entry.getKey(), entry.getValue());
						}
					} else if (payload.charAt(0)=='[') {
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
		if(ELEngine.isRootPath(express)){
			return rootDatas.get(express);
		}else {
			return DBFoundEL.getData(express, rootDatas);
		}
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
		if (object != null && !class1.isAssignableFrom(object.getClass())) {
			if (class1.equals(String.class)) {
				object = DataUtil.stringValue(object);
			} else if (class1.equals(Integer.class) || class1.equals(int.class)) {
				object = DataUtil.intValue(object);
			} else if (class1.equals(Long.class) || class1.equals(long.class)) {
				object = DataUtil.longValue(object);
			} else if (class1.equals(Float.class) || class1.equals(float.class)) {
				object = DataUtil.floatValue(object);
			} else if (class1.equals(Double.class) || class1.equals(double.class)) {
				object = DataUtil.doubleValue(object);
			} else if (class1.equals(Boolean.class) || class1.equals(boolean.class)) {
				object = DataUtil.booleanValue(object);
			} else if (class1.equals(Date.class)) {
				object = DataUtil.dateValue(object);
			} else if (class1.equals(Short.class) || class1.equals(short.class)) {
				object = DataUtil.shortValue(object);
			} else if (class1.equals(Byte.class) || class1.equals(byte.class)) {
				object = DataUtil.byteValue(object);
			}
		}
		return (T) object;
	}

	public Map<String, Object> getDatas() {
		return rootDatas;
	}

	public String getString(String express) {
		Object object = getData(express);
		return DataUtil.stringValue(object);
	}

	public Integer getInt(String express){
		Object object = getData(express);
		return DataUtil.intValue(object);
	}

	public Long getLong(String express){
		Object object = getData(express);
		return DataUtil.longValue(object);
	}

	public Float getFloat(String express){
		Object object = getData(express);
		return DataUtil.floatValue(object);
	}

	public Double getDouble(String express){
		Object object = getData(express);
		return DataUtil.doubleValue(object);
	}

	public Boolean getBoolean(String express){
		Object object = getData(express);
		return DataUtil.booleanValue(object);
	}

	public <K,V> Map<K,V> getMap(String express){
		return (Map<K, V>) getData(express);
	}

	public <T> List<T> getList(String express){
		return (List<T>) getData(express);
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
		if (name.contains(".") || name.contains("[")) {
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
		if (name.contains(".") || name.contains("[")) {
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
		if (name.contains(".") || name.contains("[")) {
			throw new DBFoundRuntimeException("on request scope, the name can not be contain '.' or '[' :" + name);
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
		if (name.contains(".") || name.contains("[")) {
			throw new DBFoundRuntimeException("on session scope, the name can not be contain '.' or '[' :" + name);
		}
		if (request != null) {
			request.getSession().setAttribute(name, value);
		}
		getSessionDatas().put(name, value);
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

				DBUtil.prepareTransaction(conn, transaction);
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

	/**
	 * 得到默认数据库连接
	 * 
	 * @return Connection
	 */
	public Connection getConn() {
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

	public void addCookie(Param param){
		Cookie cookie = new Cookie(param.getName(), param.getStringValue());
		String path = request.getContextPath();
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		cookie.setPath(path);
		cookie.setMaxAge(10 * 24 * 60 * 60);
		response.addCookie(cookie);
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

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public long getPageStart() {
		return pageStart;
	}

	public void setPageStart(long pageStart) {
		this.pageStart = pageStart;
	}

	public boolean isOutMessage() {
		return outMessage;
	}

	public void setOutMessage(boolean outMessage) {
		this.outMessage = outMessage;
	}

	public boolean onTopModelDeep(){
		return modelDeep == 0 ;
	}

	public void modelDeepIncrease() {
		modelDeep ++ ;
	}

	public void modelDeepReduce() {
		modelDeep -- ;
	}

	public CountType getCountType() {
		return countType;
	}

	public void setCountType(CountType countType) {
		this.countType = countType;
	}

	public boolean isExport() {
		return isExport;
	}

	public void setExport(boolean export) {
		isExport = export;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	private void checkContext(){
		long runThread = Thread.currentThread().getId();
		if (runThread != createThread) {
			throw new DBFoundRuntimeException("Context can not be user by different thread, create thread id:"+
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
