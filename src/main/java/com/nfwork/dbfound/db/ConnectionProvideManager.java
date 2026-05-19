package com.nfwork.dbfound.db;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;

public class ConnectionProvideManager {

	private static final Map<String, ConnectionProvide> provides = new ConcurrentHashMap<>();

	public static void destroy(DBFoundInitToken dbfoundInitToken) {
		DBFoundConfig.checkInitToken(dbfoundInitToken);
		for (ConnectionProvide provide : new ArrayList<>(provides.values())) {
			provide.unRegister();
		}
	}

	// 注册数据源
	static void registerSource(ConnectionProvide provide) {
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
	static void unRegisterSource(ConnectionProvide provide) {
		String provideName = provide.getProvideName();
		synchronized (provides) {
			provides.remove(provideName);
		}
	}

	public static ConnectionProvide getConnectionProvide(String provideName) {
		ConnectionProvide provide = provides.get(provideName);
		if (provide == null) {
			if (!DBFoundConfig.isInited() && provides.isEmpty()) {
				throw new DBFoundRuntimeException("dbfound is not initialized, please init dbfound before using ConnectionProvide");
			}
			throw new DBFoundRuntimeException("cannot find ConnectionProvide: "
					+ provideName + ", please check config");
		}
		return provide;
	}

}
