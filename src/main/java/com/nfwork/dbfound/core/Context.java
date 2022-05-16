package com.nfwork.dbfound.core;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.db.dialect.SqlDialect;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelCache;
import com.nfwork.dbfound.model.bean.Model;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;

public class Context {

	public boolean outMessage = true;
	public boolean isExport = false;
	public boolean queryLimit = DBFoundConfig.getQueryLimit();
	public int queryLimitSize = DBFoundConfig.getQueryLimitSize();
	public int reportQueryLimitSize = DBFoundConfig.getReportQueryLimitSize();
	public HttpServletRequest request;
	public HttpServletResponse response;

	private String currentPath;
	private String currentModel;
	private Map<String, ConnObject> connMap;
	private boolean inWebContainer;
	private Map<String, Object> rootDatas;
	private Map<String, Object> paramDatas;
	private Map<String, Object> outParamDatas;
	private Map<String, Object> requestDatas;
	private Map<String, Object> sessionDatas;
	private Map<String, Object> cookieDatas;
	private Map<String, Object> headerDatas;
	private static boolean openSession = true;

	private Transaction transaction = new Transaction();
	private String createThreadName = Thread.currentThread().getName();

	public Transaction getTransaction() {
		String runName = Thread.currentThread().getName();
		if (createThreadName.equals(runName) == false) {
			throw new DBFoundRuntimeException(String.format(
					"Context transaction can not user by diffrent thread，create thread:%s, run thread：%s",
					createThreadName, runName));
		}
		return transaction;
	}

	/**
	 * 得到当前的context，开启session
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static Context getCurrentContext(HttpServletRequest request, HttpServletResponse response) {
		return getCurrentContext(request, response, openSession);
	}

	/**
	 * 得到当前 context，是否需要开启session
	 * 
	 * @param request
	 * @param response
	 * @param openSession
	 * @return
	 */
	public static Context getCurrentContext(HttpServletRequest request, HttpServletResponse response,
			boolean openSession) {
		Object context = request.getAttribute("_currentContext");
		if (context == null) {
			context = new Context(request, response, openSession);
			request.setAttribute("_currentContext", context);
		}
		return (Context) context;
	}

	public Context() {
		rootDatas = new HashMap<String, Object>();
	}

	/**
	 * 根据map生成一个thread对象
	 * 
	 * @param datas
	 */
	public Context(Map<String, Object> datas) {
		if (datas == null) {
			datas = new HashMap<String, Object>();
		}
		rootDatas = datas;
	}

	private Context(HttpServletRequest request, HttpServletResponse response, boolean openSession) {
		rootDatas = new HashMap<String, Object>();

		cloneParamData(request);
		cloneRequestData(request);
		cloneHeaderData(request);
		cloneCookieData(request);
		if (openSession) {
			cloneSessionData(request.getSession());
		}

		inWebContainer = true;
		this.request = request;
		this.response = response;
	}

	/**
	 * cloneCookieData
	 * 
	 * @param request
	 */
	public void cloneCookieData(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			if (cookieDatas == null) {
				cookieDatas = new HashMap<String, Object>();
				rootDatas.put("cookie", cookieDatas);
			}
			for (Cookie cookie : cookies) {
				if (cookie.getName() != null) {
					cookieDatas.put(cookie.getName(), cookie);
				}
			}
		}
	}

	/**
	 * clone header data
	 * 
	 * @param request
	 */
	public void cloneHeaderData(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getHeaderNames();
		if (headerDatas == null) {
			headerDatas = new HashMap<String, Object>();
			rootDatas.put("header", headerDatas);
		}
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			if (key != null) {
				headerDatas.put(key, request.getHeader(key));
			}
		}
	}

	/**
	 * 复制session数据
	 */
	@SuppressWarnings("unchecked")
	public void cloneSessionData(HttpSession session) {
		if (this.openSession == false) {
			throw new DBFoundRuntimeException("session is not opened, can not set data to session ");
		}
		Enumeration<String> enumeration = session.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			if (paramName.indexOf(".") > -1) {
				continue; // 初始化复制session数据时，不克隆a.b多层次数据。
			}
			setSessionData(paramName, session.getAttribute(paramName));
		}
	}

	/**
	 * 复制param数据
	 */
	@SuppressWarnings("unchecked")
	public void cloneParamData(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			String value = request.getParameter(paramName);
			Object object;
			if (value.startsWith("{") && value.endsWith("}")) {
				object = JsonUtil.jsonToMap(value);
			} else if (value.startsWith("[") && value.endsWith("]")) {
				object = JsonUtil.jsonToList(value);
			} else {
				object = value;
			}
			setParamData(paramName, object);
		}
	}

	/**
	 * 复制request数据
	 */
	@SuppressWarnings({ "unchecked" })
	public void cloneRequestData(HttpServletRequest request) {
		Enumeration<String> enumeration = request.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			if ("_currentContext".equals(paramName)) {
				continue;
			} else if (paramName.indexOf(".") > -1) {
				continue; // 初始化复制request数据时，不克隆a.b多层次数据。
			}
			setRequestData(paramName, request.getAttribute(paramName));
		}
	}

	public Object getData(String express) {
		return DBFoundEL.getData(express, rootDatas);
	}

	/**
	 * 根据表达式得到context内容
	 * 
	 * @param express
	 * @param class1
	 * @return
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
		if (name.startsWith("request.")) {
			name = name.substring(8);
			setRequestData(name, object);
		} else if (name.startsWith("session.")) {
			name = name.substring(8);
			setSessionData(name, object);
		} else if (name.startsWith("param.")) {
			name = name.substring(6);
			setParamData(name, object);
		} else if (name.startsWith("outParam.")) {
			name = name.substring(9);
			setOutParamData(name, object);
		} else if (name.equals("param") || name.equals("outParam") || name.equals("request") || name.equals("session")) {
			throw new DBFoundRuntimeException("context inner object (param,request,session,outParam) can not set");
		} else {
			setRootData(name, object);
		}
	}

	/**
	 * 放参数到root集
	 * 
	 * @param name
	 * @param object
	 */
	private void setRootData(String name, Object object) {
		if (name.indexOf(".") > -1) {
			throw new DBFoundRuntimeException("param name can not be cotain '.' :" + name);
		}
		if (request != null) {
			request.setAttribute(name, object);
		}
		rootDatas.put(name, object);
	}

	/**
	 * 放参数到param集
	 * 
	 * @param name
	 * @param value
	 */
	public void setParamData(String name, Object value) {
		if (name.indexOf(".") > -1) {
			throw new DBFoundRuntimeException("param name can not be cotain '.' :" + name);
		}
		if (paramDatas == null) {
			Object o = rootDatas.get("param");
			if (o != null && o instanceof Map) {
				paramDatas = (Map) o;
			} else {
				paramDatas = new HashMap<String, Object>();
				rootDatas.put("param", paramDatas);
			}
		}
		if (request != null) {
			request.setAttribute(name, value);
		}
		paramDatas.put(name, value);
	}

	/**
	 * 放参数到outParam集
	 * 
	 * @param name
	 * @param object
	 */
	public void setOutParamData(String name, Object object) {
		if (name.indexOf(".") > -1) {
			throw new DBFoundRuntimeException("param name can not be cotain '.' :" + name);
		}
		if (outParamDatas == null) {
			Object o = rootDatas.get("outParam");
			if (o != null && o instanceof Map) {
				outParamDatas = (Map) o;
			} else {
				outParamDatas = new HashMap<String, Object>();
				rootDatas.put("outParam", outParamDatas);
			}
		}
		outParamDatas.put(name, object);
	}

	/**
	 * 放参数到request集
	 * 
	 * @param name
	 * @param object
	 */
	public void setRequestData(String name, Object object) {
		if (name.indexOf(".") > -1) {
			throw new DBFoundRuntimeException("param name can not be cotain '.' :" + name);
		}
		if (requestDatas == null) {
			Object o = rootDatas.get("request");
			if (o != null && o instanceof Map) {
				requestDatas = (Map) o;
			} else {
				requestDatas = new HashMap<String, Object>();
				rootDatas.put("request", requestDatas);
			}
		}
		if (request != null) {
			request.setAttribute(name, object);
		}
		requestDatas.put(name, object);
	}

	/**
	 * 放参数到session集
	 * 
	 * @param name
	 * @param object
	 */
	public void setSessionData(String name, Object object) {
		if (this.openSession == false) {
			throw new DBFoundRuntimeException("session is not opened, can not set data to session ");
		}
		if (name.indexOf(".") > -1) {
			throw new DBFoundRuntimeException("param name can not be cotain '.' :" + name);
		}
		if (sessionDatas == null) {
			Object o = rootDatas.get("session");
			if (o != null && o instanceof Map) {
				sessionDatas = (Map) o;
			} else {
				sessionDatas = new HashMap<String, Object>();
				rootDatas.put("session", sessionDatas);
			}
		}
		if (request != null) {
			request.getSession().setAttribute(name, object);
		}
		sessionDatas.put(name, object);
	}

	/**
	 * 获取model
	 * 
	 * @param modelName
	 * @return
	 */
	public Model getModel(String modelName) {
		Model model = ModelCache.get(modelName);

		if(DBFoundConfig.isModelModifyCheck()) {
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
	 * @param provideName
	 * @return
	 */
	public Connection getConn(String provideName) {

		// 校验是否夸线程
		String runName = Thread.currentThread().getName();
		if (createThreadName.equals(runName) == false) {
			throw new DBFoundRuntimeException(String.format(
					"Context transaction can not user by diffrent thread，create thread:%s, run thread：%s",
					createThreadName, runName));
		}

		if (transaction.isOpen()) {
			if (transaction.connMap == null) {
				transaction.connMap = new HashMap<String, ConnObject>();
			}
			ConnObject connObject = transaction.connMap.get(provideName);
			if (connObject == null) {
				ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
				Connection conn = provide.getConnection();
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

	/**
	 * 得到默认数据库连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConn() throws SQLException {
		return getConn("_default");
	}

	public SqlDialect getConnDialect(String provideName) {
		if (transaction.isOpen()) {
			ConnectionProvide provide = transaction.connMap.get(provideName).provide;
			return provide.getSqlDialect();
		} else {
			ConnectionProvide provide = connMap.get(provideName).provide;
			return provide.getSqlDialect();
		}
	}

	/**
	 * 关闭连接
	 */
	public void closeConns() {
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
		return outParamDatas;
	}

	public boolean isInWebContainer() {
		return inWebContainer;
	}

	public String getCreateThreadName() {
		return createThreadName;
	}
	
	public static void setOpenSession(boolean openSession) {
		Context.openSession = openSession;
	}

	static {
		DBFoundConfig.init();
	}

	class ConnObject {
		Connection connection;
		ConnectionProvide provide;

		ConnObject(ConnectionProvide provide, Connection connection) {
			this.connection = connection;
			this.provide = provide;
		}
	}
}
