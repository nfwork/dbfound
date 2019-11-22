package com.nfwork.dbfound.model.tools;

import java.util.List;


public class Table
{
  private String tableName;
  private String remarks;
  private List<Column> columnlist;

  public String getTableName()
  {
    return this.tableName.toLowerCase();
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getRemarks() {
    return this.remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public List<Column> getColumnlist() {
	return columnlist;
}

public void setColumnlist(List<Column> columnlist) {
	this.columnlist = columnlist;
}

public String toString()
  {
    return "Table [tableName=" + this.tableName + ", remarks=" + this.remarks + "]";
  }
}
