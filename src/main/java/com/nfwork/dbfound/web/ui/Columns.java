package com.nfwork.dbfound.web.ui;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.exception.TagLocationException;

public class Columns extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private List<Column> columns ;

	public int doStartTag() throws JspTagException {
		 columns = new ArrayList<Column>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, Grid.class);
		if (t == null) {
			throw new TagLocationException("标签columns位置不正确，只能在Grid标签里面使用");
		}
		
		Grid parent = (Grid) t;
		try {
			parent.setColumns((Columns) this.clone());
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_PAGE;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

}
