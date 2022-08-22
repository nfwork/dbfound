package com.nfwork.dbfound.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * 清理url中多斜杠问题
 * @author John
 *
 */
public class URLUtil {
	
	private static String BASE_PATH = "";

	/**
	 * 清理url中多斜杠问题
	 * jetty等容易不支持问题
	 */
	private static final Pattern p = Pattern.compile("/[/]+");

	public static String clearUrl(String url) {
		Matcher m = p.matcher(url);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(buf, "/");
		}
		m.appendTail(buf);
		return buf.toString();
	}

	public static String getBasePath(HttpServletRequest request) {
		String basePath ;
		
		if(DataUtil.isNotNull(BASE_PATH)) {
			basePath = BASE_PATH.replace("${@contextPath}", request.getContextPath()); 
		}else {
			StringBuffer requestUrl = request.getRequestURL();
			String servletPath = request.getServletPath();

			int index = requestUrl.indexOf(servletPath);
			
			if (index == -1) {
				basePath = requestUrl.toString();
			} else {
				basePath = requestUrl.substring(0, index);
			}
		}
		
		if (!basePath.endsWith("/")) {
			basePath = basePath + "/";
		}
		return basePath;
	}

	public static void setBasePath(String basePath) {
		URLUtil.BASE_PATH = basePath;
	}
}
