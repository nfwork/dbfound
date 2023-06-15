package com.nfwork.dbfound.web.ui;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.Tag;

import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.UUIDUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class Grid extends EventTag {
	private static final long serialVersionUID = 1L;
	private String width;
	private String height = "335";
	private String id;
	private String queryUrl;
	private Columns columns;
	private String pagerSize = "10";
	private String align = "center";
	private boolean autoQuery = false;
	private String templateName = "grid.ftl";
	private ToolBar toolBar;
	private String model;
	private String title;
	private String queryForm;
	private String viewForm;
	private String plugins;
	private String isCellEditable;
	private boolean forceFit = true;
	private boolean selectable = true;
	private boolean selectFirstRow = true;
	private boolean singleSelect;
	private boolean navBar = true;
	private boolean rowNumber = true;
	private String style = "";

	private Configuration cfg;

	public int doStartTag() throws JspTagException {
		toolBar = null;
		columns = new Columns();
		return super.doStartTag();
	}

	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		executeFreemarker(out);
		reset();
		return EVAL_PAGE;
	}

	public void executeFreemarker(Writer out) {
		try {
			cfg = FreemarkFactory.getConfig(pageContext.getServletContext());
			// 定义Template对象
			Template template = cfg.getTemplate(templateName);
			// 定义数据
			Map<String, Object> root = new HashMap<String, Object>();

			// 将转换信息放到map中 columns grid buttons

			if (id == null || "".equals(id)) {
				id = "GRID" + UUIDUtil.getRandomString(5);
			}

			if (queryUrl == null || "".equals(queryUrl)) {
				queryUrl = model + ".query";
			}
			root.put("grid", this);
			root.put("columns", columns.getColumns());

			if (toolBar != null) {
				List<GridButton> buttons = toolBar.getButtons();

				for (GridButton gridButton : buttons) {
					if ("delete".equals(gridButton.getType())) {
						if (gridButton.getAction() == null
								|| "".equals(gridButton.getAction()))
							gridButton.setAction(model + ".execute!delete");
					} else if ("save".equals(gridButton.getType())) {
						if (gridButton.getAction() == null
								|| "".equals(gridButton.getAction()))
							gridButton
									.setAction(model + ".execute!addOrUpdate");
					}
					if ("excel".equals(gridButton.getType())) {
						String url = queryUrl;
						if (url == null || url.startsWith("/")
								|| url.startsWith("../")
								|| url.startsWith("./")
								|| url.startsWith("http")) {
						} else {
							url = pageContext.getRequest().getAttribute(
									"basePath")
									+ url;
						}
						if (gridButton.getAction() == null
								|| "".equals(gridButton.getAction()))
							gridButton.setAction(url.replace(".query",
									".export"));
					}
				}
				root.put("buttons", buttons);
			}

			// 判断是不是在第一个tab页
			Object first = pageContext.getAttribute("isFirstTab");
			if (first != null && (Boolean) first == false) {
				root.put("delayRender", true);
			} else {
				root.put("delayRender", false);
			}

			String div = "<div style='margin-left:5px;margin-right:5px;margin-top:5px;margin-bottom:5px;"
					+ style + "' id='" + id + "_div'></div>";

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
		queryUrl = null;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public Columns getColumns() {
		return columns;
	}

	public void setColumns(Columns columns) {
		this.columns = columns;
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

	public String getPagerSize() {
		return pagerSize;
	}

	public void setPagerSize(String pagerSize) {
		this.pagerSize = pagerSize;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(ToolBar toolBar) {
		this.toolBar = toolBar;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = MultiLangUtil.getValue(title, pageContext);
	}

	public String getQueryForm() {
		return queryForm;
	}

	public void setQueryForm(String queryForm) {
		this.queryForm = queryForm;
	}

	public String getIsCellEditable() {
		return isCellEditable;
	}

	public void setIsCellEditable(String isCellEditable) {
		this.isCellEditable = isCellEditable;
	}

	public String getViewForm() {
		return viewForm;
	}

	public void setViewForm(String viewForm) {
		this.viewForm = viewForm;
	}

	public boolean isAutoQuery() {
		return autoQuery;
	}

	public void setAutoQuery(boolean autoQuery) {
		this.autoQuery = autoQuery;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public boolean isSingleSelect() {
		return singleSelect;
	}

	public void setSingleSelect(boolean singleSelect) {
		this.singleSelect = singleSelect;
	}

	public boolean isForceFit() {
		return forceFit;
	}

	public void setForceFit(boolean forceFit) {
		this.forceFit = forceFit;
	}

	public boolean isNavBar() {
		return navBar;
	}

	public void setNavBar(boolean navBar) {
		this.navBar = navBar;
	}

	public String getPlugins() {
		return plugins;
	}

	public void setPlugins(String plugins) {
		this.plugins = plugins;
	}

	public boolean isRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(boolean rowNumber) {
		this.rowNumber = rowNumber;
	}

	public boolean isSelectFirstRow() {
		return selectFirstRow;
	}

	public void setSelectFirstRow(boolean selectFirstRow) {
		this.selectFirstRow = selectFirstRow;
	}
	
}
