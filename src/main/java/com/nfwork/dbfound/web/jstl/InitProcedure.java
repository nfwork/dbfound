package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;

public class InitProcedure extends TagSupport implements TryCatchFinally {

	private static final long serialVersionUID = -5941376965348919531L;

	public int doStartTag() throws JspTagException {
		// 开启事务
		Transaction transaction = getContext().getTransaction();

		/*
		 * 兼容spring事务管理，initProcedure不再开启事务；
		 */
		//transaction.begin();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		Transaction transaction = getContext().getTransaction();
		transaction.commit();
		transaction.end();
		return EVAL_PAGE;
	}

	Context getContext(){
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		return Context.getCurrentContext(request, response);
	}

	private void rollbackAndEnd(){
		Transaction transaction = getContext().getTransaction();
		if(transaction.isOpen()) {
			transaction.rollback();
			transaction.end();
		}
	}

	@Override
	public void doCatch(Throwable throwable) throws Throwable {
		rollbackAndEnd();
	}

	@Override
	public void doFinally() {
		rollbackAndEnd();
	}
}
