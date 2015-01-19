package com.nfwork.dbfound.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.bean.Param;

/**
 * 静态参数 初始化
 * 
 * @author John
 * 
 */
public class ParseUtil {

	static String replaceString = "\\#\\{\\@[ a-zA-Z_0-9\u4E00-\u9FA5]*\\}";

	/**
	 * 静态参数 初始化 #{@paramName}
	 * 
	 * @param sql
	 * @param params
	 * @param outParams
	 * @return
	 */
	
	public static String parse(String sql, Map<String, Param> params) {
		if (sql == null || "".equals(sql)) {
			return "";
		}
		String paramValue;

		Pattern p = Pattern.compile(replaceString);
		Matcher m = p.matcher(sql);
		StringBuffer buf = new StringBuffer();
		while (m.find()) {
			String param = m.group();
			String pn = param.substring(3, param.length() - 1);
			Param nfParam = params.get(pn.trim());

			if (nfParam == null) {
				throw new ParamNotFoundException("param: " + pn + " 没有定义");
			}
			paramValue = nfParam.getStringValue();

			// UUID取值
			if ("true".equals(nfParam.getUUID())) {
				paramValue = UUIDUtil.getUUID();
			}else if (paramValue == null) {
				paramValue = "";
			}else {
				paramValue = paramValue.replace("$", "\\$");
			}
			m.appendReplacement(buf, paramValue);
		}
		m.appendTail(buf);
		return buf.toString();
	}
}
