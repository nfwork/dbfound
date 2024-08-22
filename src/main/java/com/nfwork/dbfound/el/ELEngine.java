package com.nfwork.dbfound.el;

import com.nfwork.dbfound.util.CollectionUtil;
import com.nfwork.dbfound.util.DataUtil;

import java.util.Set;

public class ELEngine {
	public static final String sessionScope = "session.";
	public static final String requestScope = "request.";
	public static final String paramScope = "param.";
	public static final String outParamScope = "outParam.";
	private static final Set<String> absolutePathSet = CollectionUtil.asSet("session","request","param","outParam","cookie","header");

	public static boolean isRootPath(String express){
		return absolutePathSet.contains(express);
	}
	public static boolean isAbsolutePath(String exeSourcePath){
		if (DataUtil.isNull(exeSourcePath)){
			return false;
		}
		exeSourcePath = exeSourcePath.trim();
		int index = exeSourcePath.indexOf(".");
		String rootPath;
		if(index > -1){
			rootPath = exeSourcePath.substring(0,index);
		}else{
			rootPath = exeSourcePath;
		}
		return absolutePathSet.contains(rootPath) ;
	}
}
