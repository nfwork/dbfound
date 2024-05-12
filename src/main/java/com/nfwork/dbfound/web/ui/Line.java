package com.nfwork.dbfound.web.ui;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.exception.TagLocationException;

public class Line extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private List<Field> fields;
	private String columnWidth;
	private String height;

	public int doStartTag() throws JspTagException {
		if (height == null || height.isEmpty()) {
			height = "27";
		}
		fields = new ArrayList<Field>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, Form.class);
		if (t == null) {
			throw new TagLocationException("标签line位置不正确，只能在form标签里面使用");
		}

		Form parent = (Form) t;
		try {
			Line line = (Line) this.clone();
			parent.getLines().add(line);
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}
		reset();
		return EVAL_PAGE;
	}

	public void reset() {
		height = null;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
