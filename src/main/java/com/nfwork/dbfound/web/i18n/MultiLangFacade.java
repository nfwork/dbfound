package com.nfwork.dbfound.web.i18n;

import javax.servlet.jsp.PageContext;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;

public class MultiLangFacade {

	private static I18NProvide provide;

	public static void init(DBFoundInitToken dbfoundInitToken, String className) {
		DBFoundConfig.checkInitToken(dbfoundInitToken);
		try {
			Object object = Class.forName(className.trim()).getConstructor().newInstance();
			if (object instanceof I18NProvide) {
				provide = (I18NProvide) object;
			} else {
				throw new DBFoundRuntimeException("class:" + className
						+ " does not implement I18NProvide, I18N init failed.");
			}
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	public static void destroy(DBFoundInitToken dbfoundInitToken) {
		DBFoundConfig.checkInitToken(dbfoundInitToken);
		provide = null;
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
