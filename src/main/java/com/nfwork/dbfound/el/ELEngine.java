package com.nfwork.dbfound.el;

import com.nfwork.dbfound.util.DataUtil;

public class ELEngine {
	public static final String sessionScope = "session.";
	public static final String requestScope = "request.";
	public static final String paramScope = "param.";
	public static final String outParamScope = "outParam.";
	public static final String cookieScope = "cookie.";
	public static final String headerScope = "header.";

	public static boolean isAbsolutePath(String exeSourcePath){
		if (DataUtil.isNull(exeSourcePath)){
			return false;
		}
		exeSourcePath = exeSourcePath + ".";
		return  exeSourcePath.startsWith(paramScope) || exeSourcePath.startsWith(outParamScope)
				|| exeSourcePath.startsWith(sessionScope) || exeSourcePath.startsWith(requestScope)
				|| exeSourcePath.startsWith(cookieScope) || exeSourcePath.startsWith(headerScope) ;
	}
}
