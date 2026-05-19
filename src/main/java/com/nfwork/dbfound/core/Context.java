package com.nfwork.dbfound.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.nfwork.dbfound.db.dialect.SqlDialect;
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
	private String currentModelAction;
	private ConnectionManager connectionManager;
	private final ContextData data;

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
		data = new ContextData();
		response = null;
		request = null;
	}

	/**
	 * 根据map生成一个thread对象
	 * 
	 * @param datas  map data
	 */
	public Context(Map<String, Object> datas) {
		data = new ContextData(datas);
		response = null;
		request = null;
	}

	private Context(HttpServletRequest request, HttpServletResponse response) {
		data = new ContextData(new HashMap<>(), request);

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
			throw new DBFoundRuntimeException("session is not opened, cannot set data to session ");
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
	@SuppressWarnings("unchecked")
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
						List<?> list = JsonUtil.jsonToList(payload);
						setParamData("dataList", list);
					}
				}
			}
		}catch (IOException exception){
			throw new DBFoundRuntimeException(exception);
		}
	}

	public Object getData(String express) {
		return data.getData(express);
	}

	public Object getData(String express, Map<String, Object> elCache) {
		return data.getData(express, elCache);
	}

	public int getDataLength(String express){
		return data.getDataLength(express);
	}

	/**
	 * 根据表达式得到context内容
	 * 
	 * @param express express
	 * @param class1 class
	 * @return T
	 */
	public <T> T getData(String express, Class<T> class1) {
		return data.getData(express, class1);
	}

	public Map<String, Object> getDatas() {
		return data.getDatas();
	}

	public String getString(String express) {
		return data.getString(express);
	}

	public Integer getInt(String express){
		return data.getInt(express);
	}

	public Long getLong(String express){
		return data.getLong(express);
	}

	public Float getFloat(String express){
		return data.getFloat(express);
	}

	public Double getDouble(String express){
		return data.getDouble(express);
	}

	public BigDecimal getBigDecimal(String express){
		return data.getBigDecimal(express);
	}

	public Boolean getBoolean(String express){
		return data.getBoolean(express);
	}

	public <K,V> Map<K,V> getMap(String express){
		return data.getMap(express);
	}

	public <T> List<T> getList(String express){
		return data.getList(express);
	}

	public void setData(String name, Object object) {
		data.setData(name, object);
	}

	/**
	 * 放参数到param集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setParamData(String name, Object value) {
		data.setParamData(name, value);
	}

	/**
	 * 放参数到outParam集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setOutParamData(String name, Object value) {
		data.setOutParamData(name, value);
	}

	/**
	 * 放参数到request集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setRequestData(String name, Object value) {
		data.setRequestData(name, value);
	}

	/**
	 * 放参数到session集
	 * 
	 * @param name name
	 * @param value value
	 */
	public void setSessionData(String name, Object value) {
		data.setSessionData(name, value);
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
			return transaction.getConn(provideName);
		} else {
			if (connectionManager == null) {
				connectionManager = new ConnectionManager();
			}
			return connectionManager.getConnection(provideName);
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
		if (transaction !=null && transaction.isOpen()) {
			return transaction.getConnDialect(provideName);
		} else {
			return connectionManager.getSqlDialect(provideName);
		}
	}

	/**
	 * 关闭连接
	 */
	public void closeConns() {
		checkContext();
		if (connectionManager != null) {
			connectionManager.closeConnections();
		}
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
		return data.getOutParamDatas();
	}

	public Map<String, Object> getParamDatas() {
		return data.getParamDatas();
	}

	public Map<String, Object> getRequestDatas() {
		return data.getRequestDatas();
	}

	public Map<String, Object> getSessionDatas() {
		return data.getSessionDatas();
	}

	public Map<String, Object> getCookieDatas() {
		return data.getCookieDatas();
	}

	public Map<String, Object> getHeaderDatas() {
		return data.getHeaderDatas();
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

	public String getCurrentModelAction() {
		return currentModelAction;
	}

	public void setCurrentModelAction(String currentModelAction) {
		this.currentModelAction = currentModelAction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	private void checkContext(){
		long runThread = Thread.currentThread().getId();
		if (runThread != createThread) {
			throw new DBFoundRuntimeException("Context cannot be used by different thread, create thread id:"+
					createThread + ", run thread id:" + runThread);
		}
	}

}
