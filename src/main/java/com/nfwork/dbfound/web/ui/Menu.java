package com.nfwork.dbfound.web.ui;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Menu extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String templateName = "menu.ftl";
	private List<MenuItem> items;
	private String id;
	private String width;
	private String height;

	public int doStartTag() throws JspTagException {
		items = new ArrayList<MenuItem>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		return EVAL_PAGE;
	}

	public void executeFreemarker(Writer out) {
		try {
			Configuration cfg = FreemarkerFactory.getConfig(pageContext
					.getServletContext());
			// 定义Template对象
			Template template = cfg.getTemplate(templateName);
			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("menu", this);

			template.process(root, out);
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public List<MenuItem> getItems() {
		return items;
	}

	public void setItems(List<MenuItem> items) {
		this.items = items;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
