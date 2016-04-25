package com.nfwork.dbfound.web.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;

import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.JsonUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.exception.TagLocationException;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class Field extends EventTag {
	private static final long serialVersionUID = 1L;
	private String name;
	private String hiddenName;
	private String width;
	private String height;
	private String prompt;
	private boolean readOnly = false;
	private boolean upper = false;
	private String editor;
	private boolean required = false;
	private boolean hidden;
	private String value;
	private String displayField = "name";
	private String options;
	private String valueField = "code";
	private String mode = "local"; // remote
	private String columnWidth;
	private String lovUrl;
	private String lovWidth;
	private String lovHeight;
	private String lovFunction;
	private String emptyText;
	private String anchor = "90%";
	private String checkedValue = "Y";
	private String minValue;
	private String maxValue;
	private String items;
	private boolean allowDecimals = true; // 允许小数点
	private boolean allowNegative = true; // 允许负数
	private boolean editable = true; // combo，lov之类的弹出组件 是否可以编辑
	private boolean hideLabel;
	private String vtype;
	private String parentField;
	private String currentTime;
	private String precision;

	public int doEndTag() throws JspTagException {
		Tag t = findAncestorWithClass(this, Line.class);
		if (t == null) {
			throw new TagLocationException("field标签(" + name + ")位置不正确，只能在line标签里面使用");
		}
		Line parent = (Line) t;

		try {
			Field field = (Field) this.clone();

			//当为时间输入框时，支持自动填充当前时间
			if ("datefield".equals(field.getEditor()) && DataUtil.isNull(value) && DataUtil.isNotNull(currentTime)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				date.setDate(date.getDate() + DataUtil.intValue(currentTime));
				field.setValue(format.format(date));
			} else if ("datetimefield".equals(field.getEditor()) && DataUtil.isNull(value)
					&& DataUtil.isNotNull(currentTime)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				date.setDate(date.getDate() + DataUtil.intValue(currentTime));
				field.setValue(format.format(date));
			}

			if (hidden) {
				field.setEditor("hidden");
			} else {
				if (hiddenName == null || "".equals(hiddenName)) {
					field.hiddenName = name;
				}
				if ("textarea".equals(field.getEditor())) {
					if (height == null) {
						field.setHeight("50");
					}
					int h = Integer.parseInt(field.getHeight()) + 5;
					parent.setHeight("" + h);
				} else if ("htmleditor".equals(field.getEditor())) {
					if (height == null) {
						field.setHeight("100");
					}
					int h = Integer.parseInt(field.getHeight()) + 5;
					parent.setHeight("" + h);
				} else if ("lov".equals(editor)) {
					if (field.lovHeight == null) {
						field.lovHeight = "440";
					}
					if (field.lovWidth == null) {
						field.lovWidth = "600";
					}
					field.lovFunction = "function(){$D.openLov(null,this,'" + field.name + "'," + field.lovWidth + ","
							+ field.lovHeight + ",'" + field.prompt + "');}";
				}
			}
			parent.getFields().add(field);
		} catch (CloneNotSupportedException e) {
			LogUtil.error(e.getMessage(), e);
		}
		return EVAL_PAGE;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getLovUrl() {
		return lovUrl;
	}

	public void setLovUrl(String lovUrl) {
		this.lovUrl = lovUrl;
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

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
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

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = JsonUtil.stringToJson(value);
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public boolean isUpper() {
		return upper;
	}

	public void setUpper(boolean upper) {
		this.upper = upper;
	}

	public String getHiddenName() {
		return hiddenName;
	}

	public void setHiddenName(String hiddenName) {
		this.hiddenName = hiddenName;
	}

	public String getLovFunction() {
		return lovFunction;
	}

	public void setLovFunction(String lovFunction) {
		this.lovFunction = lovFunction;
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

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		this.emptyText = MultiLangUtil.value(emptyText, pageContext);
	}

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	public String getCheckedValue() {
		return checkedValue;
	}

	public void setCheckedValue(String checkedValue) {
		this.checkedValue = checkedValue;
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

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public boolean isHideLabel() {
		return hideLabel;
	}

	public void setHideLabel(boolean hideLabel) {
		this.hideLabel = hideLabel;
	}

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}
	
}
