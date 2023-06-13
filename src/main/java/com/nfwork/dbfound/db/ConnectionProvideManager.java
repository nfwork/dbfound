package com.nfwork.dbfound.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;

public class ConnectionProvideManager {

	private static final Map<String, ConnectionProvide> provides = new ConcurrentHashMap<>();

	// 注册数据源
	static void registSource(ConnectionProvide provide) {
		String provideName = provide.getProvideName();
		synchronized (provides) {
			ConnectionProvide s = provides.get(provideName);
			if (s != null) {
				throw new DBFoundRuntimeException("ConnectionProvide named " + provideName +" already exists");
			}
			provides.put(provideName, provide);
		}
	}

	// 取消注册数据源
	static void unRegistSource(ConnectionProvide provide) {
		String provideName = provide.getProvideName();
		synchronized (provides) {
			provides.remove(provideName);
		}
	}

	public static ConnectionProvide getConnectionProvide(String provideName) {
		ConnectionProvide provide = provides.get(provideName);
		if (provide == null) {
			throw new DBFoundRuntimeException("can not found ConnectionProvide："
					+ provideName + ", please check config");
		}
		return provide;
	}

}
