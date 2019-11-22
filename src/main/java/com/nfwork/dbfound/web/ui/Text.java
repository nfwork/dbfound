package com.nfwork.dbfound.web.ui;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class Text extends TagSupport {

	private static final long serialVersionUID = 7982987692288859474L;
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
