package com.nfwork.dbfound.web.chart;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.ui.FreemarkFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class BarChart extends TagSupport {

	private static final long serialVersionUID = -8809274275750472446L;
	private String templateName = "barChart.ftl";

	private String id;
	private String title;
	private String bindTarget;
	private String width = "'100%'";
	private String height = "400";
	private String yAxis;
	private String xAxis;
	private String style = "";
	private String rotation = "0";//下方字体旋转角度
	private String valueSuffix = "";//value后面添加字符，如单位

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		id = null;
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
				id = "BC" + UUIDUtil.getRandomString(6);
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

	public String getyAxis() {
		return yAxis;
	}

	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}

	public String getxAxis() {
		return xAxis;
	}

	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}

	public String getRotation() {
		return rotation;
	}

	public void setRotation(String rotation) {
		this.rotation = rotation;
	}

	public String getValueSuffix() {
		return valueSuffix;
	}

	public void setValueSuffix(String valueSuffix) {
		this.valueSuffix = valueSuffix;
	}
	
}
