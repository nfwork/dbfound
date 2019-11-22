package com.nfwork.dbfound.db;

import java.util.HashMap;
import java.util.Map;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;

public class ConnectionProvideManager {

	private static Map<String, ConnectionProvide> provides = new HashMap<String, ConnectionProvide>();

	// 注册数据源
	static void registSource(ConnectionProvide provide) {
		String provideName = provide.getProvideName();
		synchronized (provides) {
			ConnectionProvide s = provides.get(provideName);
			if (s != null) {
				LogUtil.info("close provide (" + provides + "),release resource");
				provides.remove(provideName);
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
