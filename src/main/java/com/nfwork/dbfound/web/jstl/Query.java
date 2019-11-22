package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.MethodUtils;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.web.WebExceptionHandle;
import com.nfwork.dbfound.web.base.QueryDataProvide;

public class Query extends TagSupport implements Cloneable {

	private static final long serialVersionUID = -5941376965347019531L;
	private String queryName;
	private String modelName;
	private String sourcePath;
	private String rootPath;
	private String dataProvideClass;
	private String dataProvideMethod = "execute";

	public int doEndTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Context context = Context.getCurrentContext(request, response);
		try {
			if (dataProvideClass != null) {
				context.setData(rootPath, getData(context));
			} else {
				if (queryName == null || "".equals(queryName))
					queryName = "_default";
				QueryResponseObject ro = ModelEngine.query(context, modelName, queryName,
						sourcePath, false);
				context.setData(rootPath, ro.getDatas());
			}
			
		} catch (Exception e) {
			Transaction transaction = context.getTransaction();
			if (transaction.isOpen()) {
				transaction.rollback();
				transaction.end();
			}
			WebExceptionHandle.handle(e, request,
					(HttpServletResponse) pageContext.getResponse());
			return SKIP_PAGE;
		}

		return EVAL_PAGE;
	}

	private Object getData(Context context) throws Exception {
		Object object = Class.forName(dataProvideClass).newInstance();

		if (object instanceof QueryDataProvide) {
			Object ro = MethodUtils.invokeMethod(object, dataProvideMethod,
					new Object[] { context });
			return ro;
		} else {
			throw new DBFoundRuntimeException("Provide必须实现QueryDataProvide接口");
		}
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

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getDataProvideClass() {
		return dataProvideClass;
	}

	public void setDataProvideClass(String dataProvideClass) {
		this.dataProvideClass = dataProvideClass;
	}

	public String getDataProvideMethod() {
		return dataProvideMethod;
	}

	public void setDataProvideMethod(String dataProvideMethod) {
		this.dataProvideMethod = dataProvideMethod;
	}

}
