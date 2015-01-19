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

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ButtonGroup extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String templateName = "buttonGroup.ftl";
	private List<Button> buttons;
	private String id;
	private String align = "left";
	private String width;
	private String height;
	private String style = "";

	public int doStartTag() throws JspTagException {
		buttons = new ArrayList<Button>();
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
			Configuration cfg = FreemarkFactory.getConfig(pageContext
					.getServletContext());
			// 定义Template对象
			Template template = cfg.getTemplate(templateName);
			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();

			if (id == null || "".equals(id)) {
				id = "BG" + UUIDUtil.getRandomString(5);
			}

			root.put("buttonGroup", this);
			root.put("buttons", buttons);

			if (width != null)
				style = style + "width:" + width + ";";
			if (height != null)
				style = style + "height:" + height + ";";

			String div = "<div style='margin-top:5px;" + style + "'>"
					+ "<table id='" + id + "_table'><tr><td align='" + align
					+ "'>" + "<div id='" + id
					+ "_div' style='margin-left:5px;margin-right:5px;'></div>"
					+ "</td></tr></table></div>";

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
		style = "";
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
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

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	/**
	 * 老版本兼容性处理
	 * 
	 * @param height
	 */
	public void setHeight(int height) {
		if (height != 0) {
			this.height = "" + height;
		}
	}

	/**
	 * 老版本兼容性处理
	 * 
	 * @param height
	 */
	public void setWidth(int width) {
		if (width != 0) {
			this.width = "" + width;
		}
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
