package com.nfwork.dbfound.web.ui;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.exception.TagLocationException;

public class Event extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String handle;

	public int doEndTag() throws JspTagException {
		try {
			Tag t = findAncestorWithClass(this, EventTag.class);
			if (t != null) {
				EventTag parent = (EventTag) t;
				Event event = (Event) this.clone();
				parent.getEvents().add(event);
			} else {
				throw new TagLocationException(
						"标签event位置不正确，只能在grid、tree、field、column标签里面使用");
			}
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}
}
