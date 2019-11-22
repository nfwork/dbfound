package com.nfwork.dbfound.web.ui;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.exception.TagLocationException;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class MenuItem extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private String icon;
	private String title;
	private String click;
	private String id;
	private boolean disabled;
	private String menu;

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, Menu.class);
		if (t == null) {
			throw new TagLocationException("标签MenuItem位置不正确，只能在Menu标签里面使用");
		}
		Menu parent = (Menu) t;
		MenuItem item = null;
		try {
			item = (MenuItem) this.clone();
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}
		parent.getItems().add(item);
		return EVAL_PAGE;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title =MultiLangUtil.getValue(title,pageContext) ;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

}
