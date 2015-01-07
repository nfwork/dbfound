package com.nfwork.dbfound.el;

import javax.servlet.jsp.PageContext;

public class ELEngine {
	public static final String sessionScope = "session.";
	public static final String requestScope = "request.";
	public static final String paramScope = "param.";
	public static final String outParamScope = "outParam.";

	public static String parse(PageContext pageContext, String text)
			throws Exception {
		try {
			text = changeText(text.trim());
			return (String) ELHelper
					.evaluate(text, String.class, pageContext, null);
		} catch (Exception e) {
			return null;
		}
	}

	public static Object parseObject(PageContext pageContext, String text)
			throws Exception {
		try {
			text = changeText(text.trim());
			return ELHelper.evaluate(text, Object.class, pageContext, null);
		} catch (Exception e) {
			return null;
		}
	}

	public static String changeText(String text) {
		String returnText = "${";
		if (text.startsWith(sessionScope)) {
			returnText += "sessionScope" + text.substring(7);
		} else if (text.startsWith(requestScope)) {
			returnText += "requestScope" + text.substring(7);
		} else {
			returnText += text;
		}
		return returnText + "}";
	}
}
