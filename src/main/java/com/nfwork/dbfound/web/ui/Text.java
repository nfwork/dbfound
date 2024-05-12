package com.nfwork.dbfound.web.ui;

import java.io.IOException;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class Text extends TagSupport {

	private String value ;

	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print(MultiLangUtil.value(value, pageContext));
		} catch (IOException e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_PAGE;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
