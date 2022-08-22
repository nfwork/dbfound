package com.nfwork.dbfound.web.ui;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Panel extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static final String templateName = "panel.ftl";
	String title;
	String id;
	String height = "350";
	String width;
	boolean collapsible;
	boolean collapsed;
	String style = "";
	String html;
	String contentCmp;
	StringBuilder content;

	public int doStartTag() throws JspTagException {
		if (id == null || "".equals(id)) {
			id = "PANEL" + UUIDUtil.getRandomString(5);
		}
		content = new StringBuilder();
		contentCmp = null;
		html = "<div id='" + id + "_div'>";
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		html += "</div>";
		executeFreemarker(pageContext.getOut());
		try {
			pageContext.getOut().print(content.toString());
		} catch (IOException e) {
			LogUtil.error(e.getMessage(), e);
		}
		id = null;
		return EVAL_PAGE;
	}

	public void executeFreemarker(Writer out) {
		try {
			Configuration cfg = FreemarkFactory.getConfig(pageContext
					.getServletContext());

			Template template = cfg.getTemplate(templateName);

			// 定义数据
			Map<String, Object> root = new HashMap<>();

			root.put("panel", this);

			template.process(root, out);

		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	public String getContentCmp() {
		return contentCmp;
	}

	public void setContentCmp(String contentCmp) {
		this.contentCmp = contentCmp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = MultiLangUtil.getValue(title, pageContext);
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

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public StringBuilder getContent() {
		return content;
	}

	public void setContent(StringBuilder content) {
		this.content = content;
	}
}
