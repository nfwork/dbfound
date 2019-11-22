package com.nfwork.dbfound.excel;

public class ExcelCellMeta {

	private String content;

	private String name;

	private int width = 16;

	public ExcelCellMeta() {
	}

	public ExcelCellMeta(String name, String content,int width) {
		this.name = name;
		this.content = content;
		this.width = (int) (width * 0.15);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
