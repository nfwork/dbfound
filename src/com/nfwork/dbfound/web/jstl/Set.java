package com.nfwork.dbfound.web.jstl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.core.Context;

public class Set extends TagSupport {
	private static final long serialVersionUID = 6338686566444815853L;
	
	private String scope;
	
	private Object value;
	
	private String name;

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Context context = Context.getCurrentContext(request, response);
		if ("session".equals(scope)) {
			context.setSessionData(name, value);
		}else if ("request".equals(scope)) {
			context.setRequestData(name, value);
		}else if ("param".equals(scope)) {
			context.setParamData(name, value);
		}else if ("outParam".equals(scope)) {
			context.setOutParamData(name, value);
		}else {
			context.setData(name, value);
		}
		
		return EVAL_PAGE;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
