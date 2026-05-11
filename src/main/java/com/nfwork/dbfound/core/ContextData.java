package com.nfwork.dbfound.core;

import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
class ContextData {

	private static final String PARAM = "param";
	private static final String OUT_PARAM = "outParam";
	private static final String REQUEST = "request";
	private static final String SESSION = "session";
	private static final String COOKIE = "cookie";
	private static final String HEADER = "header";

	private final HttpServletRequest request;
	private final Map<String, Object> rootDatas;
	private Map<String, Object> paramDatas;
	private Map<String, Object> outParamDatas;
	private Map<String, Object> requestDatas;
	private Map<String, Object> sessionDatas;
	private Map<String, Object> cookieDatas;
	private Map<String, Object> headerDatas;

	ContextData() {
		this(new HashMap<>(), null);
	}

	ContextData(Map<String, Object> datas) {
		this(datas, null);
	}

	ContextData(Map<String, Object> datas, HttpServletRequest request) {
		if (datas == null) {
			datas = new HashMap<>();
		}
		this.rootDatas = datas;
		this.request = request;
	}

	Object getData(String express) {
		if(ELEngine.isRootPath(express)){
			return rootDatas.get(express);
		}else {
			return DBFoundEL.getData(express, rootDatas);
		}
	}

	Object getData(String express, Map<String, Object> elCache) {
		return DBFoundEL.getData(express, rootDatas, elCache);
	}

	int getDataLength(String express){
		Object data = this.getData(express);
		if(data == null) {
			return -1;
		}
		return DataUtil.getDataLength(data);
	}

	<T> T getData(String express, Class<T> class1) {
		Object object = getData(express);
		if (object != null && !class1.isAssignableFrom(object.getClass())) {
			object = convertData(object, class1);
		}
		return (T) object;
	}

	String getString(String express) {
		return DataUtil.stringValue(getData(express));
	}

	Integer getInt(String express){
		return DataUtil.intValue(getData(express));
	}

	Long getLong(String express){
		return DataUtil.longValue(getData(express));
	}

	Float getFloat(String express){
		return DataUtil.floatValue(getData(express));
	}

	Double getDouble(String express){
		return DataUtil.doubleValue(getData(express));
	}

	BigDecimal getBigDecimal(String express){
		return DataUtil.bigDecimalValue(getData(express));
	}

	Boolean getBoolean(String express){
		return DataUtil.booleanValue(getData(express));
	}

	<K,V> Map<K,V> getMap(String express){
		return (Map<K, V>) getData(express);
	}

	<T> List<T> getList(String express){
		return (List<T>) getData(express);
	}

	void setData(String name, Object object) {
		if(DataUtil.isNull(name)){
			throw new DBFoundRuntimeException("name cannot be null");
		}
		if (name.startsWith(ELEngine.paramScope)) {
			setParamData(name.substring(ELEngine.paramScope.length()), object);
		} else if (name.startsWith(ELEngine.outParamScope)) {
			setOutParamData(name.substring(ELEngine.outParamScope.length()), object);
		} else if (name.startsWith(ELEngine.requestScope)) {
			setRequestData(name.substring(ELEngine.requestScope.length()), object);
		} else if (name.startsWith(ELEngine.sessionScope)) {
			setSessionData(name.substring(ELEngine.sessionScope.length()), object);
		} else  {
			throw new DBFoundRuntimeException("context only in (param,request,session,outParam) can set data");
		}
	}

	void setParamData(String name, Object value) {
		setElData(name, getParamDatas(), value);
	}

	void setOutParamData(String name, Object value) {
		setElData(name, getOutParamDatas(), value);
	}

	void setRequestData(String name, Object value) {
		checkFlatScopeName(name, "request");
		if (request != null) {
			request.setAttribute(name, value);
		}
		getRequestDatas().put(name, value);
	}

	void setSessionData(String name, Object value) {
		if (!DBFoundConfig.isOpenSession()) {
			throw new DBFoundRuntimeException("session is not opened, cannot set data to session ");
		}
		checkFlatScopeName(name, "session");
		if (request != null) {
			request.getSession().setAttribute(name, value);
		}
		getSessionDatas().put(name, value);
	}

	Map<String, Object> getDatas() {
		return rootDatas;
	}

	Map<String, Object> getOutParamDatas() {
		if (outParamDatas == null) {
			outParamDatas = getOrCreateScope(OUT_PARAM);
		}
		return outParamDatas;
	}

	Map<String, Object> getParamDatas() {
		if (paramDatas == null) {
			paramDatas = getOrCreateScope(PARAM);
		}
		return paramDatas;
	}

	Map<String, Object> getRequestDatas() {
		if (requestDatas == null) {
			requestDatas = getOrCreateScope(REQUEST);
		}
		return requestDatas;
	}

	Map<String, Object> getSessionDatas() {
		if (!DBFoundConfig.isOpenSession()) {
			throw new DBFoundRuntimeException("session is not opened, cannot get data from session ");
		}
		if (sessionDatas == null) {
			sessionDatas = getOrCreateScope(SESSION);
		}
		return sessionDatas;
	}

	Map<String, Object> getCookieDatas() {
		if (cookieDatas == null) {
			cookieDatas = getOrCreateScope(COOKIE);
		}
		return cookieDatas;
	}

	Map<String, Object> getHeaderDatas() {
		if (headerDatas == null) {
			headerDatas = getOrCreateScope(HEADER);
		}
		return headerDatas;
	}

	private <T> Object convertData(Object object, Class<T> class1) {
		if (class1.equals(String.class)) {
			return DataUtil.stringValue(object);
		} else if (class1.equals(Integer.class) || class1.equals(int.class)) {
			return DataUtil.intValue(object);
		} else if (class1.equals(Long.class) || class1.equals(long.class)) {
			return DataUtil.longValue(object);
		} else if (class1.equals(Float.class) || class1.equals(float.class)) {
			return DataUtil.floatValue(object);
		} else if (class1.equals(Double.class) || class1.equals(double.class)) {
			return DataUtil.doubleValue(object);
		} else if (class1.equals(Boolean.class) || class1.equals(boolean.class)) {
			return DataUtil.booleanValue(object);
		} else if (class1.equals(BigDecimal.class)) {
			return DataUtil.bigDecimalValue(object);
		} else if (class1.equals(Date.class)) {
			return DataUtil.dateValue(object);
		} else if (class1.equals(Short.class) || class1.equals(short.class)) {
			return DataUtil.shortValue(object);
		} else if (class1.equals(Byte.class) || class1.equals(byte.class)) {
			return DataUtil.byteValue(object);
		}
		return object;
	}

	private void setElData(String name, Map<String, Object> datas, Object value) {
		if (name.contains(".") || name.contains("[")) {
			DBFoundEL.setData(name, datas, value);
		}else{
			datas.put(name, value);
		}
	}

	private void checkFlatScopeName(String name, String scope) {
		if (name.contains(".") || name.contains("[")) {
			throw new DBFoundRuntimeException("on " + scope + " scope, the name cannot contain '.' or '[' :" + name);
		}
	}

	private Map<String, Object> getOrCreateScope(String scope) {
		Object object = rootDatas.get(scope);
		if (object instanceof Map) {
			return (Map<String, Object>) object;
		}
		Map<String, Object> datas = new HashMap<>();
		rootDatas.put(scope, datas);
		return datas;
	}
}
