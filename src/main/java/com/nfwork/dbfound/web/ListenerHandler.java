package com.nfwork.dbfound.web;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.base.Listener;

public final class ListenerHandler {

	public static synchronized void init(String className) {
		try {
			Object object = Class.forName(className).getConstructor().newInstance();
			if (object instanceof Listener) {
				Listener listener = (Listener) object;
				listener.init();
			} else {
				throw new DBFoundRuntimeException("class:" + className
						+ ", not implements com.nfwork.dbfound.web.base.Listener");
			}
		} catch (Exception e) {
			LogUtil.error("Listener init failed", e);
		}
	}
}
