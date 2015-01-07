package com.nfwork.dbfound.web.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nfwork.dbfound.core.Context;

public interface Interceptor {

	public boolean jspInterceptor(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public boolean queryInterceptor(Context context, String modelName,
			String queryName) throws Exception;

	public boolean executeInterceptor(Context context, String modelName,
			String executeName) throws Exception;

	public boolean exportInterceptor(Context context, String modelName,
			String queryName) throws Exception;

	public boolean doInterceptor(Context context, String className,
			String method) throws Exception;

	public void init();
}
