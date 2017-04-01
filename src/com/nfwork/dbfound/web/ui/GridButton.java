package com.nfwork.dbfound.web.ui;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.exception.TagLocationException;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class GridButton extends TagSupport implements Cloneable {
	private static final long serialVersionUID = 1L;
	private String icon;
	private String type = "others";
	private String action;
	private String title;
	private String beforeAction;
	private String afterAction = "null";
	
	private String id;
	private boolean disabled ;
	private boolean showConfirm;
	private String comfirmMessage;

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, ToolBar.class);
		if (t == null) {
			throw new TagLocationException("标签gridButton位置不正确，只能在toolBar标签里面使用");
		}

		ToolBar parent = (ToolBar) t;

		GridButton button = null;
		try {
			button = (GridButton) this.clone();
			if (button.getId() == null || "".equals(button.getId())) {
				button.setId("GB" + UUIDUtil.getRandomString(6));
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title =MultiLangUtil.getValue(title,pageContext) ;
	}

	public boolean isShowConfirm() {
		return showConfirm;
	}

	public void setShowConfirm(boolean showConfirm) {
		this.showConfirm = showConfirm;
	}

	public String getComfirmMessage() {
		return comfirmMessage;
	}

	public void setComfirmMessage(String comfirmMessage) {
		this.comfirmMessage = MultiLangUtil.getValue(comfirmMessage,pageContext) ;;
	}

	public String getBeforeAction() {
		return beforeAction;
	}

	public void setBeforeAction(String beforeAction) {
		this.beforeAction = beforeAction;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAfterAction() {
		return afterAction;
	}

	public void setAfterAction(String afterAction) {
		this.afterAction = afterAction;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
}
