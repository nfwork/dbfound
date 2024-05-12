package com.nfwork.dbfound.web.ui;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Tabs extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String width;
	private String height;
	private String id;
	private static final String templateName = "tabs.ftl";
	private boolean plain = true;
	private String style = "";
	private List<Tab> tabs;

	public int doStartTag() throws JspTagException {
		// 设置第一个tab页标示
		pageContext.setAttribute("isFirstTab", true);

		tabs = new ArrayList<Tab>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		pageContext.removeAttribute("isFirstTab");
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		for (Tab tab : tabs) {
			try {
				if (tab.content != null) {
					out.print(tab.content.toString());
				}
			} catch (IOException e) {
				LogUtil.error(e.getMessage(), e);
			}
		}
		reset();
		return EVAL_PAGE;
	}

	public void executeFreemarker(Writer out) {
		try {
			Configuration cfg = FreemarkerFactory.getConfig(pageContext
					.getServletContext());

			Template template = cfg.getTemplate(templateName);

			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();

			if (id == null || id.isEmpty()) {
				id = "TABS" + UUIDUtil.getRandomString(5);
			}

			root.put("t", this);
			root.put("tabs", tabs);

			template.process(root, out);

		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	private void reset() {
		id = null;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Tab> getTabs() {
		return tabs;
	}

	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}

	public boolean isPlain() {
		return plain;
	}

	public void setPlain(boolean plain) {
		this.plain = plain;
	}
}
