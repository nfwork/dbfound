package com.nfwork.dbfound.web.chart;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.ui.FreemarkerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class PieChart extends TagSupport {

	private static final long serialVersionUID = -8809274275750472446L;
	private String templateName = "pieChart.ftl";

	private String id;
	private String title;
	private String bindTarget;
	private String width = "'100%'";
	private String height = "400";
	private String displayField;
	private String valueField;
	private boolean dataLabel;
	private String style = "";
	private String valueSuffix = "";//value后面添加字符，如单位

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		id = null;
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

			if (id == null || "".equals(id)) {
				id = "PC" + UUIDUtil.getRandomString(6);
			}
			root.put("chart", this);
			template.process(root, out);
		} catch (Exception e) {
			LogUtil.error(e.getMessage(), e);
		}
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
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
		this.title = title;
	}

	public String getBindTarget() {
		return bindTarget;
	}

	public void setBindTarget(String bindTarget) {
		this.bindTarget = bindTarget;
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

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public boolean isDataLabel() {
		return dataLabel;
	}

	public void setDataLabel(boolean dataLabel) {
		this.dataLabel = dataLabel;
	}

	public String getValueSuffix() {
		return valueSuffix;
	}

	public void setValueSuffix(String valueSuffix) {
		this.valueSuffix = valueSuffix;
	}
	
}
