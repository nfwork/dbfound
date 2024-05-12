package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

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
