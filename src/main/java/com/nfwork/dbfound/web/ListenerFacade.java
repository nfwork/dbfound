package com.nfwork.dbfound.web;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.base.Listener;

import javax.servlet.ServletContext;

public final class ListenerFacade {

	private static Listener listener;

	public static synchronized void init(String className, ServletContext servletContext) {
		try {
			Object object = Class.forName(className).getConstructor().newInstance();
			if (object instanceof Listener) {
				listener = (Listener) object;
				listener.init(servletContext);
			} else {
				throw new DBFoundRuntimeException("class:" + className
						+ ", not implements com.nfwork.dbfound.web.base.Listener");
			}
		} catch (Exception e) {
			throw new DBFoundRuntimeException("Listener init failed", e);
		}
	}

	public static void destroy(){
		if(listener != null){
			try {
				listener.destroy();
			} catch (Throwable throwable) {
				LogUtil.error("Listener init failed", throwable);
			}
		}
	}
}
