package com.nfwork.dbfound.web.ui;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class Events extends TagSupport {

	private static final long serialVersionUID = -5548202519156183002L;

	public int doStartTag() throws JspTagException {
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		return EVAL_PAGE;
	}
}
