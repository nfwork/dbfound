package com.nfwork.dbfound.web.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;

public interface Interceptor {

	boolean jspInterceptor(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	boolean queryInterceptor(Context context, String modelName,
			String queryName) throws Exception;

	boolean executeInterceptor(Context context, String modelName,
			String executeName) throws Exception;

	boolean exportInterceptor(Context context, String modelName,
			String queryName) throws Exception;

	boolean doInterceptor(Context context, String className,
			String method) throws Exception;

	void init();

	default void setCors(HttpServletRequest request,HttpServletResponse response){}

}
