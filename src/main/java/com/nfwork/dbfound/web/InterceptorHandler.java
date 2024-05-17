package com.nfwork.dbfound.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.base.Interceptor;

public class InterceptorHandler {

	static Interceptor interceptor;

	static boolean inited = false;

	public static synchronized void init(String className) {
		try {
			Object object = Class.forName(className).getConstructor().newInstance();
			if (object instanceof Interceptor) {
				interceptor = (Interceptor) object;
				interceptor.init();
				inited = true;
			} else {
				throw new DBFoundRuntimeException("class:" + className
						+ ", not implements com.nfwork.dbfound.web.base.AccessFilter");
			}
		} catch (Exception e) {
			LogUtil.error("access filter init failed", e);
		}
	}

	public static void setCorsMapping(HttpServletRequest request,HttpServletResponse response){
		if (inited) {
			interceptor.setCorsMapping(request, response);
		}
	}

	public static boolean jspInterceptor(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (inited) {
			return interceptor.jspInterceptor(request, response);
		} else {
			return true;
		}
	}

	public static boolean queryInterceptor(Context context, String modelName,
			String queryName) throws Exception {
		if (inited) {
			return interceptor.queryInterceptor(context, modelName, queryName);
		} else {
			return true;
		}
	}

	public static boolean executeInterceptor(Context context, String modelName,
			String executeName) throws Exception {
		if (inited) {
			return interceptor.executeInterceptor(context, modelName,
					executeName);
		} else {
			return true;
		}
	}

	public static boolean exportInterceptor(Context context, String modelName,
			String queryName) throws Exception {
		if (inited) {
			return interceptor.exportInterceptor(context, modelName, queryName);
		} else {
			return true;
		}
	}

	public static boolean doInterceptor(Context context, String className,
			String method) throws Exception {
		if (inited) {
			return interceptor.doInterceptor(context, className, method);
		} else {
			return true;
		}
	}
}
