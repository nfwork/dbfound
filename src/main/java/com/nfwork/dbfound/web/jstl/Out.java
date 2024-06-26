package com.nfwork.dbfound.web.jstl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.web.WebWriter;

public class Out extends TagSupport {
	private static final long serialVersionUID = 6448686546444875853L;

	private Object value;

	public int doEndTag() throws JspException {
		if (value instanceof List) {
			value = JsonUtil.toJson(value);
		} else if (value instanceof Map) {
			value = JsonUtil.toJson(value);
		}
		WebWriter.jsonWriter((HttpServletResponse) pageContext.getResponse(),
				value.toString());
		return SKIP_PAGE;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
