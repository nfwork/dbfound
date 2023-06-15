package com.nfwork.dbfound.web.i18n;

import jakarta.servlet.jsp.PageContext;

public interface I18NProvide {

	public String value(String code, PageContext pageContext);
}
