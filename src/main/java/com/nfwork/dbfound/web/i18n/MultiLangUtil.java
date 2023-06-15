package com.nfwork.dbfound.web.i18n;

import jakarta.servlet.jsp.PageContext;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;

public class MultiLangUtil {

	private static I18NProvide provide;

	public static void init(String className) {
		try {
			Object object = Class.forName(className.trim()).newInstance();
			if (object instanceof I18NProvide) {
				provide = (I18NProvide) object;
			} else {
				throw new DBFoundRuntimeException("class:" + className
						+ ",不是I18NProvide类的实现，I18N初始化失败。");
			}
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	public static String value(String code, PageContext pageContext) {
		if (provide == null) {
			return code;
		} else {
			return provide.value(code, pageContext);
		}
	}

	public static String getValue(String code, PageContext pageContext) {
		if (code.startsWith("i18n:")) {
			code = code.substring(5);
			return value(code, pageContext);
		}
		return code;
	}
}
