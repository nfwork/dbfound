package com.nfwork.dbfound.web.ui;

import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.exception.TagLocationException;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class Tab extends Panel implements Cloneable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String id;
	private String url;
	private String initUrl = "";
	private boolean closable;
	private String height;

	public int doStartTag() throws JspTagException {
		if (id == null || id.isEmpty()) {
			id = "TAB" + UUIDUtil.getRandomString(5);
		}
		html = "<div id='" + id + "_div'>";
		contentCmp = null;
		content = new StringBuilder();
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		// 设置第一个tab页标示 为flase
		pageContext.setAttribute("isFirstTab", false);

		html += "</div>";
		Tag t = findAncestorWithClass(this, Tabs.class);
		if (t == null) {
			throw new TagLocationException("标签Tab位置不正确，只能在Tabs标签里面使用");
		}
		Tabs parent = (Tabs) t;
		Tab tab = null;
		try {
			tab = (Tab) this.clone();
			if (url == null || url.startsWith("/") || url.startsWith("../")
					|| url.startsWith("./") || url.startsWith("http")) {
			} else {
				tab.setUrl(pageContext.getRequest().getAttribute("basePath")
						+ tab.getUrl());
			}
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}

		List<Tab> tabs = parent.getTabs();
		if (tabs.isEmpty()) {
			tab.initUrl = url;
		}
		tabs.add(tab);
		id = null;
		return EVAL_PAGE;
	}

	public String getContentCmp() {
		return contentCmp;
	}

	public void setContentCmp(String contentCmp) {
		this.contentCmp = contentCmp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = MultiLangUtil.getValue(title, pageContext);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInitUrl() {
		return initUrl;
	}

	public void setInitUrl(String initUrl) {
		this.initUrl = initUrl;
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

}
