package com.nfwork.dbfound.model.tools;

public class Column {
	private String columnName;
	private int dataType;
	private String type;
	private String remarks;
	private int columnSize;
	private boolean nullAble;
	private String defaultValue;
	private boolean autoIncrement;
	private String paramName;

	public String getColumnName() {
		return this.columnName.toLowerCase();
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getDataType() {
		return this.dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getColumnSize() {
		return this.columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public boolean isNullAble() {
		return this.nullAble;
	}

	public void setNullAble(String nullAble) {
		if ("YES".equals(nullAble))
			this.nullAble = true;
		else
			this.nullAble = false;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	public void setAutoIncrement(String autoIncrement) {
		if ("YES".equals(autoIncrement))
			this.autoIncrement = true;
		else
			this.autoIncrement = false;
	}

	public String getExpress(){
		return "${@"+paramName+"}";
	}
	
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return "Column [columnName=" + this.columnName + ", dataType="
				+ this.dataType + ", remarks=" + this.remarks + ", columnSize="
				+ this.columnSize + ", nullAble=" + this.nullAble
				+ ", defaultValue=" + this.defaultValue + ", autoIncrement="
				+ this.autoIncrement + "]";
	}
}
