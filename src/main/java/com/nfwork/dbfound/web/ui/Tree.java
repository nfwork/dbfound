package com.nfwork.dbfound.web.ui;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.Tag;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Tree extends EventTag {

	private static final long serialVersionUID = 7982827692288859474L;
	private String templateName = "tree.ftl";
	private String title;
	private String id;
	private String bindTarget;
	private String displayField;
	private String idField;
	private String parentField;
	private boolean showCheckBox;
	private String width;
	private String height;
	private String style = "";

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		reset();
		return EVAL_PAGE;
	}

	public void executeFreemarker(Writer out) {
		try {
			Configuration cfg = FreemarkFactory.getConfig(pageContext
					.getServletContext());
			// 定义Template对象
			Template template = cfg.getTemplate(templateName);
			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();

			if (id == null || "".equals(id)) {
				id = "TREE" + UUIDUtil.getRandomString(5);
			}
			root.put("tree", this);

			String div = "<div style='margin-left:5px;margin-top:5px;margin-bottom:5px;"
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

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getBindTarget() {
		return bindTarget;
	}

	public void setBindTarget(String bindTarget) {
		this.bindTarget = bindTarget;
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}

	public boolean isShowCheckBox() {
		return showCheckBox;
	}

	public void setShowCheckBox(boolean showCheckBox) {
		this.showCheckBox = showCheckBox;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = MultiLangUtil.getValue(title, pageContext);
	}

}
