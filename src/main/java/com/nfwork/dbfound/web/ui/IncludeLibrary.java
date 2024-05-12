package com.nfwork.dbfound.web.ui;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.URLUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class IncludeLibrary extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static final String templateName = "includeLibrary.ftl";

	public int doEndTag() throws JspTagException {
		JspWriter out = pageContext.getOut();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		Context context = Context.getCurrentContext(request, response);
		try {
			Configuration cfg = FreemarkerFactory.getConfig(pageContext.getServletContext());
			// 定义Template对象
			Template template = cfg.getTemplate(templateName);
			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();
			
			String basePath = DataUtil.stringValue(request.getAttribute("basePath"));
			if(DataUtil.isNull(basePath)){
				basePath = URLUtil.getBasePath(request);
				context.setRequestData("basePath", basePath);
			}
			root.put("basePath", basePath);
			template.process(root, out);
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_PAGE;
	}

}
