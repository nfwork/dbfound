package com.nfwork.dbfound.el;

import com.nfwork.dbfound.util.CollectionUtil;
import com.nfwork.dbfound.util.DataUtil;

import java.util.Set;

public class ELEngine {
	public static final String sessionScope = "session.";
	public static final String requestScope = "request.";
	public static final String paramScope = "param.";
	public static final String outParamScope = "outParam.";
	public static final String cookieScope = "cookie.";
	public static final String headerScope = "header.";

	private static final Set<String> absolutePathSet = CollectionUtil.asSet(sessionScope,requestScope,paramScope,outParamScope,cookieScope,headerScope);

	public static boolean isAbsolutePath(String exeSourcePath){
		if (DataUtil.isNull(exeSourcePath)){
			return false;
		}
		exeSourcePath = exeSourcePath.trim();
		int index = exeSourcePath.indexOf(".");
		String rootPath;
		if(index > -1){
			rootPath = exeSourcePath.substring(0,index+1);
		}else{
			rootPath = exeSourcePath + ".";
		}
		return absolutePathSet.contains(rootPath) ;
	}
}
