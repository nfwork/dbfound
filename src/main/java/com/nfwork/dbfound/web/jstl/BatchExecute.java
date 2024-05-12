package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.web.WebExceptionHandler;

public class BatchExecute extends TagSupport {

	private static final long serialVersionUID = -5941376965347019531L;
	private String name;
	private String modelName;
	private String sourcePath;

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Context context = Context.getCurrentContext(request, response);
		if (name == null || name.isEmpty())
			name = "addOrUpdate";
		try {
			ModelEngine.batchExecute(context, modelName, name, sourcePath);
		} catch (Exception e) {
			WebExceptionHandler.handle(e, request,(HttpServletResponse) pageContext.getResponse());
			return SKIP_PAGE;
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

}
