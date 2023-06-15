package com.nfwork.dbfound.web.ui;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.Tag;
import jakarta.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.exception.TagLocationException;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class Button extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private String icon;
	private String title;
	private String click;
	private String id;
	private boolean disabled ;
	private String width = "75";
	private String height = "23";

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, ButtonGroup.class);
		if (t == null) {
			throw new TagLocationException("标签button位置不正确，只能在buttonGroup标签里面使用");
		}
		ButtonGroup parent = (ButtonGroup) t;
		Button button = null;
		try {
			button = (Button) this.clone();
			if (button.getId() == null || "".equals(button.getId())) {
				button.setId("B" + UUIDUtil.getRandomString(6));
			}
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}
		parent.getButtons().add(button);
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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
	
}
