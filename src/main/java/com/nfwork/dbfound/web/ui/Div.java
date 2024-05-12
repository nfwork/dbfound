package com.nfwork.dbfound.web.ui;

import java.io.IOException;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;

public class Div extends TagSupport {
	private static final long serialVersionUID = 1L;
	private String id;
	private String style = "";

	public int doStartTag() throws JspTagException {
		if (id == null || id.isEmpty()) {
			id = "DIV" + UUIDUtil.getRandomString(5);
		}
		style = "display:none;" + style;

		String div = "<div id='" + id + "_outdiv'></div>";
		Tag t = findAncestorWithClass(this, Panel.class);
		if (t != null) {
			Panel parent = (Panel) t;
			parent.html += div;
		}
		try {
			pageContext.getOut().print(
					"<div id='" + id + "_div' style='" + style + "'>");
		} catch (IOException e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print("</div>");
			String script = "<script type='text/javascript'>em = Ext.get('"
					+ id + "_outdiv');if(em){var el = Ext.get('" + id
					+ "_div');em.appendChild(el);document.getElementById('"
					+ id + "_div').style.display='';}</script>";
			Tag t = findAncestorWithClass(this, Panel.class);
			if (t != null) {
				Panel parent = (Panel) t;
				parent.content.append(script);
			}
		} catch (IOException e) {
			LogUtil.error(e.getMessage(), e);
		}
		reset(); //重置属性
		return EVAL_PAGE;
	}

	public void reset() {
		id = null;
		style = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
}
