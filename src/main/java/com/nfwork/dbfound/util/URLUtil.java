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

	/**
	 * 清理url中多斜杠问题
	 * jetty等容易不支持问题
	 * @param url
	 * @return
	 */
	public static String clearUrl(String url) {
		Pattern p = Pattern.compile("/[/]+");
		Matcher m = p.matcher(url);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(buf, "/");
		}
		m.appendTail(buf);
		return buf.toString();
	}
	
	/**
	 * 设置basePath
	 * 
	 * @param request
	 */
	public static String getBasePath(HttpServletRequest request) {
		/*
		 * String basePath = request.getContextPath(); if
		 * (basePath.endsWith("/") == false) { basePath = basePath + "/"; }
		 */
		StringBuffer requestUrl = request.getRequestURL();
		String servletPath = request.getServletPath();

		int index = requestUrl.indexOf(servletPath);
		String basePath = "";
		if (index == -1) {
			basePath = requestUrl.toString();
		} else {
			basePath = requestUrl.substring(0, index);
		}
		if (basePath.endsWith("/") == false) {
			basePath = basePath + "/";
		}
		return basePath;
	}
}
