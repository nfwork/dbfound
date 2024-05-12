package com.nfwork.dbfound.web.ui;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Form extends TagSupport {
	private String width;
	private String labelWidth;
	private String height;
	private String id;
	private String title;
	private String bindTarget;
	private static final String templateName = "form.ftl";
	private List<Line> lines;
	private ToolBar toolBar;
	private boolean fileUpload;
	private boolean collapsible;
	private boolean collapsed;
	private String style = "";

	public int doStartTag() throws JspTagException {
		toolBar = null;
		lines = new ArrayList<Line>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		reset();
		return EVAL_PAGE;
	}

	public void executeFreemarker(Writer out) {
		try {
			Configuration cfg = FreemarkerFactory.getConfig(pageContext
					.getServletContext());

			Template template = cfg.getTemplate(templateName);

			// 定义数据
			Map<String, Object> root = new HashMap<>();

			if (id == null || id.isEmpty()) {
				id = "FORM" + UUIDUtil.getRandomString(5);
			}

			if (toolBar != null) {
				List<GridButton> buttons = toolBar.getButtons();
				if (buttons != null && !buttons.isEmpty()) {
					root.put("buttons", buttons);
				}
			}

			root.put("form", this);
			root.put("lines", lines);

			// 判断是不是在第一个tab页
			Object first = pageContext.getAttribute("isFirstTab");
			if (first != null && !((Boolean) first)) {
				root.put("delayRender", true);
			} else {
				root.put("delayRender", false);
			}

			String div = "<div style='margin-left:5px;margin-right:5px;margin-top:5px;margin-bottom:5px;"
					+ style + "' id='" + id + "_div'></div>";

			Tag t = findAncestorWithClass(this, Panel.class);
			if (t != null && findAncestorWithClass(this, Div.class) == null) {
				Panel parent = (Panel) t;
				if (parent.contentCmp == null) {
					parent.contentCmp = "'" + id + "'";
				} else {
					parent.contentCmp += ",'" + id + "'";
				}
				try {
					out = new StringWriter();
					parent.html += div;
					template.process(root, out);
					parent.content.append(out);
				} finally {
					out.close();
				}
			} else {
				root.put("div", div);
				template.process(root, out);
			}
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	private void reset() {
		id = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = MultiLangUtil.getValue(title, pageContext);
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(String labelWidth) {
		this.labelWidth = labelWidth;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public boolean isFileUpload() {
		return fileUpload;
	}

	public void setFileUpload(boolean fileUpload) {
		this.fileUpload = fileUpload;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(ToolBar toolBar) {
		this.toolBar = toolBar;
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

	public String getBindTarget() {
		return bindTarget;
	}

	public void setBindTarget(String bindTarget) {
		this.bindTarget = bindTarget;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
}
