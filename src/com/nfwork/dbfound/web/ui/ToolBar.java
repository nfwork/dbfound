package com.nfwork.dbfound.web.ui;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.TagLocationException;

public class ToolBar extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private List<GridButton> buttons;
	private String align;

	public int doStartTag() throws JspTagException {
		buttons = new ArrayList<GridButton>();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, Grid.class);
		if (t == null) {
			t = findAncestorWithClass(this, Form.class);
			if (t == null)
				throw new TagLocationException(
						"标签toolBar位置不正确，只能在grid或form标签里面使用");
			Form parent = (Form) t;
			try {
				ToolBar toolBar = (ToolBar) this.clone();
				parent.setToolBar(toolBar);
			} catch (CloneNotSupportedException e) {
				throw new DBFoundPackageException(e.getMessage(), e);
			}
			return EVAL_PAGE;
		} else {
			Grid parent = (Grid) t;
			try {
				ToolBar toolBar = (ToolBar) this.clone();
				parent.setToolBar(toolBar);
			} catch (CloneNotSupportedException e) {
				throw new DBFoundPackageException(e.getMessage(), e);
			}
			return EVAL_PAGE;
		}
	}

	public List<GridButton> getButtons() {
		return buttons;
	}

	public void setButtons(List<GridButton> buttons) {
		this.buttons = buttons;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}
}
