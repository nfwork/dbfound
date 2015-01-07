package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;

public class InitProcedure extends TagSupport {

	private static final long serialVersionUID = -5941376965348919531L;

	public int doStartTag() throws JspTagException {

		// 开启事务
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		Context context = Context.getCurrentContext(request, response);
		Transaction transaction = context.getTransaction();
		transaction.begin();

		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {

		// 提交关闭事务
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		Context context = Context.getCurrentContext(request, response);
		Transaction transaction = context.getTransaction();
		transaction.commit();
		transaction.end();

		return EVAL_PAGE;
	}

}
