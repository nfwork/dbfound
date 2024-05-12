package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;
import jakarta.servlet.jsp.tagext.TryCatchFinally;

import com.nfwork.dbfound.core.Context;

public class InitProcedure extends TagSupport implements TryCatchFinally {

	private static final long serialVersionUID = -5941376965348919531L;

	public int doStartTag() throws JspTagException {
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	Context getContext(){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		return Context.getCurrentContext(request, response);
	}

	@Override
	public void doCatch(Throwable throwable){

	}

	@Override
	public void doFinally() {

	}
}
