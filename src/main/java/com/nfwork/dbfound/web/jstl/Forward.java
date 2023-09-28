package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.WebWriter;

public class Forward extends TagSupport implements Cloneable {

	private static final long serialVersionUID = -5941376965347019531L;
	private String queryName = "_default";
	private String modelName;

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			HttpServletResponse response = (HttpServletResponse) pageContext
					.getResponse();
			Context context = Context.getCurrentContext(request, response);
			QueryResponseObject ro = ModelEngine.query(context, modelName, queryName, null,
					true);
			
			Transaction transaction = context.getTransaction();
			if (transaction.isOpen()) {
				transaction.commit();
				transaction.end();
			}
			WebWriter.jsonWriter((HttpServletResponse) pageContext
					.getResponse(), JsonUtil.toJson(ro));
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
		return SKIP_PAGE;
	}

	public String getQueryName() {
		return queryName;
	}



	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}



	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
}
