package com.nfwork.dbfound.web.ui;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.Tag;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.exception.TagLocationException;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class Column extends EventTag {
	private static final long serialVersionUID = 1L;
	private String name;
	private String width = "100";
	private String prompt;
	private String renderer;
	private String editor;
	private String align;
	private boolean required = false;
	private String displayField = "name";
	private String options;
	private String valueField = "code";
	private String mode = "local"; // remote
	private boolean locked = false;
	private boolean upper = false;
	private boolean sortable = false;
	private String lovUrl;
	private String lovWidth;
	private String lovHeight;
	private boolean allowDecimals = true; // 允许小数点
	private boolean allowNegative = true; // 允许负数
	private boolean editable = true; // combo，lov之类的弹出组件 是否可以编辑
	private boolean hidden;
	private String vtype;
	private String precision;

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, Columns.class);
		if (t == null) {
			throw new TagLocationException("column标签" + name + "位置不正确，只能在columns标签里面使用");
		}
		Columns columns = (Columns) t;
		Column column;
		try {
			column = (Column) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new DBFoundRuntimeException(e);
		}

		if (column.editor != null && !column.editor.isEmpty()) {
			// 是否为空加载
			String editorConfig;
			if (column.required) {
				editorConfig = "{allowBlank:false";
			} else {
				editorConfig = "{allowBlank:true";
			}

			if (!column.editable) {
				editorConfig = editorConfig + ",editable : false";
			}

			if (column.vtype != null) {
				editorConfig = editorConfig + ",vtype : '" + vtype + "'";
			}

			// 添加事件
			StringBuilder listeners = new StringBuilder(",enableKeyEvents : true,listeners: {");
			if (upper) {
				listeners.append("keypress :function(t, e){DBFound.keypress(t, e,this);},blur:function(){DBFound.blurUpper(this);},");
			}
			for (Event event : events) {
				if ("enter".equals(event.getName())) {
					listeners.append("specialKey:function(field,e){if(e.getKey()==Ext.EventObject.ENTER)").append(event.getHandle()).append("(field,e);}");
				} else {
					listeners.append(event.getName()).append(":").append(event.getHandle()).append(",");
				}
			}
			if(listeners.charAt(listeners.length()-1) == ','){
				listeners.deleteCharAt(listeners.length()-1);
				listeners.append("}");
				editorConfig = editorConfig + listeners;
			}

			// 编辑器加载
			if ("textfield".equals(column.editor)) {
				editorConfig = editorConfig + "}";
				column.editor = "new Ext.form.TextField(" + editorConfig + ")";
			} else if ("numberfield".equals(column.editor)) {
				if (!allowDecimals) {
					editorConfig = editorConfig + ",allowDecimals:false";
				}
				if (!allowNegative) {
					editorConfig = editorConfig + ",allowNegative:false";
				}
				editorConfig = editorConfig + "}";
				column.editor = "new Ext.form.NumberField(" + editorConfig + ")";
			} else if ("datefield".equals(column.editor)) {
				editorConfig = editorConfig + ",format:'Y-m-d'";
				editorConfig = editorConfig + "}";
				column.editor = "new Ext.form.DateField(" + editorConfig + ")";
				
				// 在用户没有指定renderer时，使用默认的renderer
				if (DataUtil.isNull(column.renderer)) {
					column.renderer = "$D.dateFormat";
				}
			} else if ("combo".equals(column.editor)) {
				editorConfig = editorConfig + ",store:" + options;
				editorConfig = editorConfig + ",displayField:'" + displayField + "'";
				editorConfig = editorConfig + ",valueField:'" + valueField + "'";
				editorConfig = editorConfig + ", triggerAction:'all' ";
				editorConfig = editorConfig + ",selectOnFocus:true ";
				editorConfig = editorConfig + ",mode : '" + mode + "'";
				editorConfig = editorConfig + "}";
				column.editor = "new Ext.form.ComboBox(" + editorConfig + ")";

				// 在用户没有指定renderer时，使用默认的renderer
				if (DataUtil.isNull(column.renderer)) {
					column.renderer = "function(value){return DBFound.gridComboRenderer(value,'" + valueField + "','"
							+ displayField + "'," + options + ");}";
				}
			} else if ("lov".equals(column.editor)) {
				if (column.lovHeight == null) {
					column.lovHeight = "440";
				}
				if (column.lovWidth == null) {
					column.lovWidth = "600";
				}
				Grid g = (Grid) findAncestorWithClass(this, Grid.class);
				if (lovUrl == null)
					lovUrl = "null";
				String lovFunction = "function(){$D.openLov(" + g.getId() + ",this,'" + column.name + "',"
						+ column.lovWidth + "," + column.lovHeight + ",'" + column.prompt + "');}";
				editorConfig = editorConfig + ",lovUrl:'" + lovUrl
						+ "',triggerClass:'x-form-search-trigger',onTriggerClick:" + lovFunction + "}";
				column.editor = "new Ext.form.TriggerField(" + editorConfig + ")";
			} else if ("lovcombo".equals(column.editor)) {
				editorConfig = editorConfig + ",store:" + options;
				editorConfig = editorConfig + ",displayField:'" + displayField + "'";
				editorConfig = editorConfig + ",valueField:'" + valueField + "'";
				editorConfig = editorConfig + ", triggerAction:'all' ";
				editorConfig = editorConfig + ",selectOnFocus:true ";
				editorConfig = editorConfig + ",mode : '" + mode + "'";
				editorConfig = editorConfig + "}";
				column.editor = "new Ext.ux.form.LovCombo(" + editorConfig + ")";
				
				// 在用户没有指定renderer时，使用默认的renderer
				if (DataUtil.isNull(column.renderer)) {
					column.renderer = "function(value){return DBFound.gridLovComboRenderer(value,'" + valueField
							+ "','" + displayField + "'," + options + ");}";
				}
			} else if ("datetimefield".equals(column.editor)) {
				if (DataUtil.isNotNull(precision)) {
					editorConfig = editorConfig + ", precision:'"+precision+"'";
				}
				editorConfig = editorConfig + "}";
				column.editor = "new Ext.ux.form.DateTimeField(" + editorConfig + ")";
				
				// 在用户没有指定renderer时，使用默认的renderer
				if (DataUtil.isNull(column.renderer)) {
					column.renderer = "function(v,m,r){return $D.dateTimeFormat(v,'" + name + "',r)}";
				}
			} else if ("password".equals(column.editor)) {
				editorConfig = editorConfig + ",inputType:'password'}";
				column.editor = "new Ext.form.TextField(" + editorConfig + ")";
				
				// 在用户没有指定renderer时，使用默认的renderer
				if (DataUtil.isNull(column.renderer)) {
					column.renderer = "$D.passwordHidden";
				}
			} else {
				column.editor = null;
			}
		}

		columns.getColumns().add(column);
		return EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = MultiLangUtil.getValue(prompt, pageContext);
	}

	public String getRenderer() {
		return renderer;
	}

	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public boolean isUpper() {
		return upper;
	}

	public void setUpper(boolean upper) {
		this.upper = upper;
	}

	public String getLovUrl() {
		return lovUrl;
	}

	public void setLovUrl(String lovUrl) {
		this.lovUrl = lovUrl;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getLovWidth() {
		return lovWidth;
	}

	public void setLovWidth(String lovWidth) {
		this.lovWidth = lovWidth;
	}

	public String getLovHeight() {
		return lovHeight;
	}

	public void setLovHeight(String lovHeight) {
		this.lovHeight = lovHeight;
	}

	public boolean isAllowDecimals() {
		return allowDecimals;
	}

	public void setAllowDecimals(boolean allowDecimals) {
		this.allowDecimals = allowDecimals;
	}

	public boolean isAllowNegative() {
		return allowNegative;
	}

	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
}
