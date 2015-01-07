package com.nfwork.dbfound.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 清理url中多斜杠问题
 * 
 * @param length
 * @return
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
}
