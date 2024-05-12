package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.web.WebExceptionHandler;

public class Execute extends TagSupport {

	private static final long serialVersionUID = -5941376965347019531L;

	private String executeName;
	private String modelName;
	private String sourcePath;

	@Override
	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Context context = Context.getCurrentContext(request, response);
		try {
			if (executeName == null || executeName.isEmpty())
				executeName = "_default";
			ModelEngine.execute(context, modelName, executeName, sourcePath);
			
		} catch (Exception e) {
			WebExceptionHandler.handle(e, request,(HttpServletResponse) pageContext.getResponse());
			return SKIP_PAGE;
		}

		return EVAL_BODY_INCLUDE;
	}

	public String getExecuteName() {
		return executeName;
	}

	public void setExecuteName(String executeName) {
		this.executeName = executeName;
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
